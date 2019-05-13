package com.dili.ss.mvc.boot;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

//@Configuration
public class BaseWebMvcConfigurationSupport extends WebMvcConfigurationSupport {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(null)
                .addPathPatterns("/**")
                .excludePathPatterns("/resources/**");
    }
}
