package com.dili.ss.retrofitful.aop.service;

import com.dili.ss.retrofitful.aop.invocation.Invocation;

/**
 * ezrestful接口服务
 */
public interface RestfulService {


    Object invoke(Invocation invocation);

}
