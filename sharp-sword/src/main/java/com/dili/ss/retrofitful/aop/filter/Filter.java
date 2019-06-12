package com.dili.ss.retrofitful.aop.filter;

import com.dili.ss.retrofitful.aop.invocation.Invocation;

/**
 * restful接口的专用切面接口
 */
public interface Filter {

    void setRestfulFilter(Filter restfulFilter);

    Filter getRestfulFilter();

    Object invoke(Invocation invocation) throws Exception;
}
