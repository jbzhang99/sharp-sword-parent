package com.dili.ss.quartz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.http.okhttp.OkHttpUtils;
import com.dili.http.okhttp.callback.Callback;
import com.dili.ss.util.AopTargetUtils;
import com.dili.ss.util.SpringUtil;
import com.dili.ss.quartz.domain.QuartzConstants;
import com.dili.ss.quartz.domain.ScheduleJob;
import com.dili.ss.quartz.domain.ScheduleMessage;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wangmi
 * @Description: 调用任务方法
 */
public class TaskUtils {
	public final static Logger log = LoggerFactory.getLogger(TaskUtils.class);
	static OkHttpClient okHttpClient = null;
	//任务处理对象缓存
	//包括java类, spring bean无法使用缓存，因为数据库连接会断开
	static Map<String, Object> cachedJobHashMap = new HashMap<>();
	static{
		okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
				.connectTimeout(10000L, TimeUnit.MILLISECONDS)
				.readTimeout(10000L, TimeUnit.MILLISECONDS)
				//其他配置
				.build();
		OkHttpUtils.initClient(okHttpClient);
	}

	/**
	 * 通过反射调用scheduleJob中定义的方法
	 * 
	 * @param scheduleJob 调度方式
	 * @param scheduleMessage 调度消息
	 */
	public static boolean invokeMethod(ScheduleJob scheduleJob, ScheduleMessage scheduleMessage) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
		if(null == scheduleJob || null == scheduleMessage){
			return true;
		}
		String timesKey = scheduleJob.getJobGroup()+scheduleJob.getJobName();
		if(!QuartzConstants.sheduelTimes.containsKey(timesKey)){
			QuartzConstants.sheduelTimes.put(timesKey,0);
		}
		QuartzConstants.sheduelTimes.put(timesKey,QuartzConstants.sheduelTimes.get(timesKey)+1);
		scheduleMessage.setSheduelTimes(QuartzConstants.sheduelTimes.get(timesKey));
		scheduleMessage.setJobGroup(scheduleJob.getJobGroup());
		scheduleMessage.setJobName(scheduleJob.getJobName());
		Object object = null;
		if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
			object = SpringUtil.getBean(scheduleJob.getSpringId());
			return invokeLocalMethod(scheduleJob, scheduleMessage, object);
		} else if (StringUtils.isNotBlank(scheduleJob.getBeanClass())) {
			if(!cachedJobHashMap.containsKey(scheduleJob.getSpringId())){
				Class clazz = Class.forName(scheduleJob.getBeanClass());
				cachedJobHashMap.put(scheduleJob.getSpringId(), clazz.newInstance());
			}
			object = cachedJobHashMap.get(scheduleJob.getBeanClass());
			return invokeLocalMethod(scheduleJob, scheduleMessage, object);
		}else if(StringUtils.isNotBlank(scheduleJob.getUrl())){
			if(scheduleJob.getIsConcurrent()!= null && scheduleJob.getIsConcurrent()==1){
				return asyncExecute(scheduleJob.getUrl(), JSONObject.toJSONString(scheduleMessage), "POST");
			}else{
				return syncExecute(scheduleJob.getUrl(), JSONObject.toJSONString(scheduleMessage), "POST");
			}
		}else{
			log.info("无可调度的对象!");
			return true;
		}
//		log.info("任务名称 = [" + scheduleJob.getJobName() + "]----------启动成功");
	}

	private static boolean invokeLocalMethod(ScheduleJob scheduleJob, ScheduleMessage scheduleMessage, Object object){
		if (object == null) {
			log.error("任务名称 = [" + scheduleJob.getJobName() + "]---------------未启动成功，请检查是否配置正确！！！");
			return false;
		}
		Method method = null;
		Object targetObj = null;
		try {
			targetObj = AopTargetUtils.getTarget(object);
			method = targetObj.getClass().getDeclaredMethod(scheduleJob.getMethodName(), ScheduleMessage.class);
			if (method != null && targetObj != null) {
				method.invoke(targetObj, scheduleMessage);
			}
			return true;
		} catch (Exception e) {
			String message = e.getMessage();
			if(e instanceof InvocationTargetException){
				message = ((InvocationTargetException) e).getTargetException().getMessage();
			}
			log.error("任务名称 = [" + scheduleJob.getJobName() + "]---------------未启动成功，调度参数设置错误！异常:" + message);
			return false;
		}
	}

	/**
	 * 同步调用远程方法
	 * @param url
	 * @param paramObj
	 * @param httpMethod
	 */
	private static boolean syncExecute(String url, Object paramObj, String httpMethod){
		Response resp = null;
		try{
			Map<String, String> headersMap = new HashMap<>(1);
			headersMap.put("Content-Type", "application/json;charset=utf-8");
			if("POST".equalsIgnoreCase(httpMethod)){

				String json = paramObj instanceof String ? (String)paramObj : JSON.toJSONString(paramObj);
				resp = OkHttpUtils
						.postString()
						.url(url).content(json)
						.mediaType(MediaType.parse("application/json; charset=utf-8"))
						.build()
						.execute();
			}else{
				resp = OkHttpUtils
						.get()
						.url(url).params((Map)JSON.toJSON(paramObj))
						.build()
						.execute();
			}
			if(resp.isSuccessful()){
				log.info(String.format("远程调用["+url+"]成功,code:[%s], message:[%s]", resp.code(),resp.message()));
				return true;
			}else{
				log.error(String.format("远程调用["+url+"]发生失败,code:[%s], message:[%s]", resp.code(),resp.message()));
				return false;
			}
		} catch (Exception e) {
			log.error(String.format("远程调用["+url+"]发生异常, message:[%s]", e.getMessage()));
			return false;
		}
	}

	/**
	 * 异步调用远程方法
	 * @param url
	 * @param paramObj
	 * @param httpMethod
	 */
	private static boolean asyncExecute(String url, Object paramObj, String httpMethod){
		try{
			Map<String, String> headersMap = new HashMap<>(1);
			headersMap.put("Content-Type", "application/json;charset=utf-8");
			if("POST".equalsIgnoreCase(httpMethod)){
				String json = paramObj instanceof String ? (String)paramObj : JSON.toJSONString(paramObj);
				OkHttpUtils
						.postString()
						.url(url).content(json)
						.mediaType(MediaType.parse("application/json; charset=utf-8"))
						.build()
						.execute(new Callback() {
							@Override
							public Object parseNetworkResponse(Response response, int id) throws Exception {
								return null;
							}

							@Override
							public void onError(Call call, Exception e, int id) {
							}

							@Override
							public void onResponse(Object response, int id) {
							}
						});
			}else{
				OkHttpUtils
						.get()
						.url(url).params((Map)JSON.toJSON(paramObj))
						.build()
						.execute(new Callback() {
							@Override
							public Object parseNetworkResponse(Response response, int id) throws Exception {
								return null;
							}

							@Override
							public void onError(Call call, Exception e, int id) {
							}

							@Override
							public void onResponse(Object response, int id) {
							}
						});
			}
			log.info("异步远程调用["+url+"]完成");
			return true;
		} catch (Exception e) {
			log.error(String.format("异步远程调用["+url+"]发生异常,message:[%s]", e.getMessage()));
			return false;
		}
	}
}
