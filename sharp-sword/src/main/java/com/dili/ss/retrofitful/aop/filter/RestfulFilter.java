package com.dili.ss.retrofitful.aop.filter;

import com.dili.http.okhttp.utils.B;
import com.dili.ss.retrofitful.aop.annotation.Order;
import com.dili.ss.retrofitful.aop.invocation.Invocation;
import com.dili.ss.retrofitful.aop.service.RestfulService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 默认的restful拦截器，最后执行
 */
@Component
@Order(Integer.MAX_VALUE)
public class RestfulFilter extends AbstractFilter {
    private RestfulService restfulService;

    @PostConstruct
    public void init(){
        Class<?> clazz = (Class<?>)B.b.g("restfulService");
        if(clazz != null) {
            try {
                restfulService = (RestfulService) clazz.newInstance();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public Object invoke(Invocation invocation) throws Exception {
        return restfulService.invoke(invocation);
    }


}
