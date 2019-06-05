package com.dili.ss.seata.annotation;

import java.lang.annotation.*;

/**
 * 全局异常注解
 */
@Inherited
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalTx {

}
