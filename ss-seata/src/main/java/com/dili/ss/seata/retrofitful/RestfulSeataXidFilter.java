package com.dili.ss.seata.retrofitful;

import com.dili.ss.retrofitful.annotation.ReqHeader;
import com.dili.ss.retrofitful.aop.annotation.Order;
import com.dili.ss.retrofitful.aop.filter.AbstractFilter;
import com.dili.ss.retrofitful.aop.invocation.Invocation;
import com.dili.ss.seata.annotation.GlobalTx;
import com.dili.ss.seata.consts.SeataConsts;
import io.seata.core.context.RootContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于在ezrestful框架中拦截RPC调用时，在header中设置XID
 */
@Component
@Order(100)
public class RestfulSeataXidFilter extends AbstractFilter {

    @Override
    public Object invoke(Invocation invocation) throws Exception {
        //初始化LogContext
        Method method = invocation.getMethod();
        GlobalTx globalTx = method.getAnnotation(GlobalTx.class);
        //方法上没有@GlobalTx，则不在header中设置XID
        if(globalTx == null){
            return super.invoke(invocation);
        }
        Annotation[][] ass = method.getParameterAnnotations();
        //没有参数也不设置XID(参数中必须要有@ReqHeader注解的Map形参)
        if(ass == null || ass.length == 0){
            return super.invoke(invocation);
        }
        retry:
        for(int i=0; i<ass.length; i++) {
            for (int j = 0; j < ass[i].length; j++) {
                Annotation annotation = ass[i][j];
                if (ReqHeader.class.equals(annotation.annotationType())) {
                    Map<String, String> headerMap = (Map)invocation.getArgs()[i];
                    if(headerMap == null) {
                        headerMap = new HashMap<>(1);
                        invocation.getArgs()[i] = headerMap;
                    }
                    String xid = RootContext.getXID();
                    if (!StringUtils.isEmpty(xid)) {
                        headerMap.put(SeataConsts.XID, xid);
                    }
                    break retry;
                }
            }
        }
        return super.invoke(invocation);
    }
}
