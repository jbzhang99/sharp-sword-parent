package com.dili.ss.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author asiam
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface GenDTOMethod {

    /**
     * 是否重复使用
     * @return
     */
    boolean reuse() default false;
}
