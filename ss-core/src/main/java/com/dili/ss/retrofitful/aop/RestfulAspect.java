package com.dili.ss.retrofitful.aop;

import java.lang.reflect.Method;

/**
 * restful接口的专用切面接口
 */
public interface RestfulAspect {

    Object around(ProceedingPoint proceedingPoint);
}
