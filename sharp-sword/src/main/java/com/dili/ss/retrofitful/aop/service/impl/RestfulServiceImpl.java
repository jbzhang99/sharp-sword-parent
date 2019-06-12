package com.dili.ss.retrofitful.aop.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dili.http.okhttp.OkHttpUtils;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.dto.IDTO;
import com.dili.ss.exception.AppException;
import com.dili.ss.exception.ParamErrorException;
import com.dili.ss.retrofitful.RestfulAnnotation;
import com.dili.ss.retrofitful.annotation.*;
import com.dili.ss.retrofitful.aop.invocation.Invocation;
import com.dili.ss.retrofitful.aop.service.RestfulService;
import com.dili.ss.util.DateUtils;
import com.dili.ss.util.POJOUtils;
import com.dili.ss.util.SystemConfigUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ezrestful 服务实现
 */
public class RestfulServiceImpl implements RestfulService {

    protected static final Logger logger = LoggerFactory.getLogger(RestfulServiceImpl.class);

    @Override
    public Object invoke(Invocation invocation){
        Restful restful = invocation.getProxyClazz().getAnnotation(Restful.class);
        String baseUrl = getBaseUrl(restful);
        if(StringUtils.isBlank(baseUrl)){
            throw new ParamErrorException("Restful注解参数值["+restful.baseUrl()+"]为空或属性不存在!");
        }
        return doHttp(invocation.getMethod(), invocation.getArgs(), baseUrl);
    }

    @SuppressWarnings("unchecked")
    private Object doHttp(Method method, Object[] args, String baseUrl){
        RestfulAnnotation restfulAnnotation = getRestfulAnnotation(method, args);
        String httpMethod = StringUtils.isBlank(restfulAnnotation.getPost()) ? "GET" : "POST";
        String url = "POST".equals(httpMethod) ? baseUrl + restfulAnnotation.getPost() : baseUrl + restfulAnnotation.getGet();
        return DelegateService.doHttp(url, method.getGenericReturnType(), restfulAnnotation, httpMethod);
    }
    @SuppressWarnings("unchecked")
    private String getBaseUrl(Restful restful){
        String baseUrl = StringUtils.isBlank(restful.baseUrl()) ? restful.value().trim() : restful.baseUrl().trim();
        if(baseUrl.startsWith("${") && baseUrl.endsWith("}")){
            String key = baseUrl.substring(2, baseUrl.length()-1).trim();
            return SystemConfigUtils.getProperty(key);
        }else{
            return baseUrl;
        }
    }

    /**
     * 根据方法获取所有注解内容
     * 可能会抛出参数校验异常
     * @param method
     * @return
     */
    @SuppressWarnings("unchecked")
    private static RestfulAnnotation getRestfulAnnotation(Method method, Object[] args){
        RestfulAnnotation restfulAnnotation = new RestfulAnnotation();
        Annotation[][] ass = method.getParameterAnnotations();
        Parameter[] parameters = method.getParameters();
        POST post = method.getAnnotation(POST.class);
        GET get = method.getAnnotation(GET.class);
        if(post != null){
            if(StringUtils.isBlank(post.value())){
                throw new ParamErrorException("@POST参数不能为空");
            }
            restfulAnnotation.setPost(post.value());
        }else if(get != null){
            if(StringUtils.isBlank(get.value())){
                throw new ParamErrorException("@GET参数不能为空");
            }
            restfulAnnotation.setGet(get.value());
        }else{
            throw new ParamErrorException("@GET和@POST注解不存在");
        }
        for(int i=0; i<ass.length; i++){
            for(int j=0; j<ass[i].length; j++){
                Annotation annotation = ass[i][j];
                if(VOField.class.equals(annotation.annotationType())) {
                    restfulAnnotation.getVoFields().put(((VOField) annotation).value(), args[i]);
                }else if(VOBody.class.equals(annotation.annotationType())){
                    restfulAnnotation.setVoBody(args[i]);
                }else if(ReqHeader.class.equals(annotation.annotationType())){
                    restfulAnnotation.setHeaders((Map)args[i]);
                }else if(ReqParam.class.equals(annotation.annotationType())){
                    Object param = args[i];
                    ReqParam reqParam = (ReqParam)annotation;
                    if(reqParam.required() && param == null){
                        throw new ParamErrorException("@ReqParam必填参数为空");
                    }
                    if(param == null){
                        //当ReqParam为null时，POST传参在okhttputil会空指针，这里处理为空串
                        restfulAnnotation.getReqParams().put(reqParam.value(), "");
                        continue;
                    }
                    if(Date.class.equals(parameters[i].getType())){
                        param = DateUtils.format((Date) param);
                    }else if(List.class.equals(parameters[i].getType())){
                        param = JSONArray.toJSONString(param, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.IgnoreErrorGetter);
                    }
                    //IDTO接口
                    else if(DTOUtils.isDTOProxy(parameters[i].getType())){
                        param = JSONObject.toJSONString(DTOUtils.go(param), SerializerFeature.WriteDateUseDateFormat, SerializerFeature.IgnoreErrorGetter);
                    }
                    //实现了IDTO的实体Bean
                    else if(IDTO.class.isAssignableFrom(parameters[i].getType())){
                        param = JSONObject.toJSONString(param, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.IgnoreErrorGetter);
                    }
                    restfulAnnotation.getReqParams().put(reqParam.value(), param.toString());
                }
            }
        }
        return restfulAnnotation;
    }

    /**
     * 委托服务
     */
    private static class DelegateService {

        protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());
        static OkHttpClient okHttpClient;
        private static final Long CONN_TIMEOUT = 10000L;
        private static final Long READ_TIMEOUT = 300000L;
        private static final Long WRITE_TIMEOUT = 300000L;

        static {
            //必须调用初始化
            okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(false)
//                .addInterceptor(new LoggerInterceptor("TAG"))
                    .connectTimeout(CONN_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                    //其他配置
                    .build();
            OkHttpUtils.initClient(okHttpClient);
        }
        DelegateService(){
        }
        @SuppressWarnings("unchecked")
        private static Object doHttp(String url, Type retType, RestfulAnnotation restfulAnnotation, String httpMethod){
            Object body = restfulAnnotation.getVoBody();
            Map<String, Object> fields = restfulAnnotation.getVoFields();
            //如果body和fields都不为空，并且body又是Map或DTO，将fields put到body中
            if(body != null) {
                if(!fields.isEmpty()) {
                    if (Map.class.isAssignableFrom(body.getClass())) {
                        ((Map) body).putAll(fields);
                    }else if(DTOUtils.isDTOProxy(body)){
                        DTOUtils.go(body).putAll(fields);
                    }
                }
                return executeBody(url, body, restfulAnnotation.getHeaders(), retType, httpMethod);
            }else if(!fields.isEmpty()){
                return executeBody(url, fields, restfulAnnotation.getHeaders(), retType, httpMethod);
            }else if(!restfulAnnotation.getReqParams().isEmpty()){
                return executeParam(url, restfulAnnotation.getReqParams(), restfulAnnotation.getHeaders(), retType, httpMethod);
            }else{
                return executeBody(url, null, restfulAnnotation.getHeaders(), retType, httpMethod);
            }
        }

        /**
         * 以request body请求方式发送参数
         * @param url
         * @param paramObj
         * @param type
         * @param httpMethod
         * @return
         */
        @SuppressWarnings("unchecked")
        private static Object executeBody(String url, Object paramObj, Map<String, String> headers, Type type, String httpMethod){
            if(!(type instanceof Class) ) {
                //远程调用失败，构建一个BaseOutput
                BaseOutput output = new BaseOutput();
                try{
                    Response resp = requestBody(url, paramObj, headers, httpMethod);
                    if (resp.isSuccessful()) {
                        String result = resp.body().string();
                        return JSON.parseObject(result, type);
                    } else {
                        String msg = String.format("远程调用失败, URL:[%s],参数:[%s],消息:[%s]", url, paramObj, resp.body().string());
//                        logger.error("远程调用失败,code:" + resp.code() + ", message:" + resp.message() + ", url:" + url + ", 参数:" + paramObj);
                        output.setCode(ResultCode.APP_ERROR);
                        output.setResult(msg);
                        output = JSON.parseObject(JSON.toJSONString(output, SerializerFeature.WriteDateUseDateFormat , SerializerFeature.IgnoreErrorGetter), type);
                    }
                } catch (Exception e) {
                    String msg = String.format("远程调用异常, URL:[%s],参数:[%s],消息:[%s]", url, paramObj, e.getMessage());
                    logger.error(msg);
                    output.setCode(ResultCode.APP_ERROR);
                    output.setResult(msg);
                    //解决：java.lang.ClassCastException:BaseOutput cannot be cast to PageOutput
                    output = JSON.parseObject(JSON.toJSONString(output, SerializerFeature.WriteDateUseDateFormat , SerializerFeature.IgnoreErrorGetter), type);
                }
                return output;
            }else{
                logger.warn("远程调用返回值建议使用BaseOutput!");
                try {
                    Class retClazz = (Class) type;
                    Response resp = requestBody(url, paramObj, headers, httpMethod);
                    if (resp.isSuccessful()) {
                        if(Void.TYPE.equals(retClazz)){
                            return null;
                        }
                        String result = resp.body().string();
                        if (retClazz.isPrimitive()) {
                            return POJOUtils.getPrimitiveValue(retClazz, result);
                        }
                        return JSON.parseObject(result, type);
                    }else{
                        String msg = (String.format("远程调用发生异常code:[%s], message:[%s]", resp.code(), resp.message())) + ", url:" + url + ", 参数:" + paramObj;
                        logger.error(msg);
                        throw new AppException(msg);
                    }
                } catch (Exception e) {
                    String msg = String.format("远程调用异常, URL:[%s],参数:[%s],消息:[%s]", url, paramObj, e.getMessage());
                    logger.error(msg);
                    throw new AppException(msg);
                }
            }
        }

        /**
         * 以request param请求方式发送参数
         * @param url
         * @param params
         * @param type
         * @param httpMethod
         * @return
         */
        @SuppressWarnings("unchecked")
        private static Object executeParam(String url, Map<String, String> params, Map<String, String> headers, Type type, String httpMethod){
            if(!(type instanceof Class) ) {
                //远程调用失败，构建一个BaseOutput
                BaseOutput output = new BaseOutput();
                try{
                    Response resp = requestParam(url, params, headers, httpMethod);
                    if (resp.isSuccessful()) {
                        String result = resp.body().string();
                        return JSON.parseObject(result, type);
                    } else {
                        String msg = String.format("远程调用失败, URL:[%s],参数:[%s],消息:[%s]", url, params, resp.body().string());
//                        logger.info("远程调用失败,code:" + resp.code() + ", message:" + resp.message() + ", url:" + url + ", 参数:" + params);
                        logger.info(msg);
                        output.setCode(ResultCode.APP_ERROR);
                        output.setResult(msg);
                        output = JSON.parseObject(JSON.toJSONString(output, SerializerFeature.WriteDateUseDateFormat , SerializerFeature.IgnoreErrorGetter), type);
                    }
                } catch (Exception e) {
                    String msg = String.format("远程调用异常, URL:[%s],参数:[%s],消息:[%s]", url, params, e.getMessage());
                    logger.info(msg);
                    output.setCode(ResultCode.APP_ERROR);
                    output.setResult(msg);
                    //解决：java.lang.ClassCastException:BaseOutput cannot be cast to PageOutput
                    output = JSON.parseObject(JSON.toJSONString(output, SerializerFeature.WriteDateUseDateFormat , SerializerFeature.IgnoreErrorGetter), type);
                }
                return output;
            }else{
                logger.warn("远程调用返回值建议使用BaseOutput!");
                try {
                    Class retClazz = (Class) type;
                    Response resp = requestParam(url, params, headers, httpMethod);
                    if (resp.isSuccessful()) {
                        if(Void.TYPE.equals(retClazz)){
                            return null;
                        }
                        String result = resp.body().string();
                        if (retClazz.isPrimitive()) {
                            return POJOUtils.getPrimitiveValue(retClazz, result);
                        }
                        return JSON.parseObject(result, type);
                    }else{
                        String msg = String.format("远程调用失败, URL:[%s],参数:[%s],消息:[%s]", url, params, resp.body().string());
                        logger.error(msg);
                        throw new AppException(msg);
                    }
                } catch (Exception e) {
                    String msg = String.format("远程调用异常, URL:[%s],参数:[%s],消息:[%s]", url, params, e.getMessage());
                    logger.error(msg);
                    throw new AppException(msg);
                }
            }
        }

        /**
         * 以requestBody方式发送请求参数
         * @param url
         * @param paramObj
         * @param httpMethod
         * @return
         * @throws IOException
         */
        @SuppressWarnings("unchecked")
        private static Response requestBody(String url, Object paramObj, Map<String, String> headers, String httpMethod) throws IOException {
            if(headers == null){
                headers = new HashMap<>(1);
            }
            if(!headers.containsKey("Content-Type")){
                headers.put("Content-Type", "application/json;charset=utf-8");
            }
            if ("POST".equalsIgnoreCase(httpMethod)) {
                String json = paramObj instanceof String ? (String) paramObj : JSON.toJSONString(paramObj, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.IgnoreErrorGetter);
                return OkHttpUtils
                        .postString().headers(headers)
                        .url(url).content(json)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .connTimeOut(CONN_TIMEOUT)
                        .readTimeOut(READ_TIMEOUT)
                        .writeTimeOut(WRITE_TIMEOUT)
                        .execute();
            } else {
                return OkHttpUtils
                        .get().headers(headers)
                        .url(url).params((Map) JSON.toJSON(paramObj))
                        .build()
                        .connTimeOut(CONN_TIMEOUT)
                        .readTimeOut(READ_TIMEOUT)
                        .writeTimeOut(WRITE_TIMEOUT)
                        .execute();
            }
        }


        /**
         * 以requestParam方式发送请求参数
         * @param url
         * @param paramObj
         * @param headers
         * @param httpMethod
         * @return
         * @throws IOException
         */
        @SuppressWarnings("unchecked")
        private static Response requestParam(String url, Map<String, String> paramObj, Map<String, String> headers, String httpMethod) throws IOException {
            if ("POST".equalsIgnoreCase(httpMethod)) {
                return OkHttpUtils
                        .post().headers(headers)
                        .url(url).params(paramObj)
                        .build()
                        .connTimeOut(CONN_TIMEOUT)
                        .readTimeOut(READ_TIMEOUT)
                        .writeTimeOut(WRITE_TIMEOUT)
                        .execute();
            } else {
                return OkHttpUtils
                        .get().headers(headers)
                        .url(url).params(paramObj)
                        .build()
                        .connTimeOut(CONN_TIMEOUT)
                        .readTimeOut(READ_TIMEOUT)
                        .writeTimeOut(WRITE_TIMEOUT)
                        .execute();
            }
        }
    }
}
