package com.dili.ss.retrofitful.annotation;

import java.lang.annotation.*;

/**
 * 请求参数
 * Created by asiamastor on 2016/11/28.
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReqParam {
    String value();
    boolean required() default true;
}
