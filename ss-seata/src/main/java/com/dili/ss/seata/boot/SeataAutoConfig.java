package com.dili.ss.seata.boot;

import io.seata.spring.annotation.GlobalTransactionScanner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@ConditionalOnExpression("'${seata.enable}'=='true'")
public class SeataAutoConfig {

    /**
     * init global transaction scanner
     *
     * @Return: GlobalTransactionScanner
     */
    @Bean
    public GlobalTransactionScanner globalTransactionScanner(Environment env){
        return new GlobalTransactionScanner(env.getProperty("spring.application.name"), env.getProperty("spring.cloud.alibaba.seata.tx-service-group"));
    }

    /**
     * http请求拦截器，用于绑定xid
     * @return
     */
    @Bean
    public OncePerRequestFilter seataXidFilter(){
        return new SeataXidFilter();
    }
}
