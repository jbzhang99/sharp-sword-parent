package com.dili.ss.seata.boot;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fescar.rm.datasource.DataSourceProxy;
import com.alibaba.fescar.spring.annotation.GlobalTransactionScanner;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: heshouyou
 * @Description  seata global configuration
 * @Date Created in 2019/1/24 10:28
 */
@Configuration
@ConditionalOnExpression("'${seata.ds.enable}'=='true'")
public class SeataDatasourceConfig {

    @Autowired
    private DataSourceProperties dataSourceProperties;

//    @Primary
//    @Bean
////    @ConfigurationProperties(prefix = "spring.datasource.druid")
//    public DataSourceProxy dataSource() {
//        DruidDataSource druidDataSource = new DruidDataSource();
//        druidDataSource.setUrl(dataSourceProperties.getUrl());
//        druidDataSource.setUsername(dataSourceProperties.getUsername());
//        druidDataSource.setPassword(dataSourceProperties.getPassword());
//        druidDataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
//        druidDataSource.setInitialSize(0);
//        druidDataSource.setMaxActive(180);
//        druidDataSource.setMaxWait(60000);
//        druidDataSource.setMinIdle(0);
//        druidDataSource.setValidationQuery("Select 1 from DUAL");
//        druidDataSource.setTestOnBorrow(false);
//        druidDataSource.setTestOnReturn(false);
//        druidDataSource.setTestWhileIdle(true);
//        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
//        druidDataSource.setMinEvictableIdleTimeMillis(25200000);
//        druidDataSource.setRemoveAbandoned(true);
//        druidDataSource.setRemoveAbandonedTimeout(1800);
//        druidDataSource.setLogAbandoned(true);
//        return new DataSourceProxy(druidDataSource);
//    }

    @Bean("druidDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public Map<String, String> druidDataSourceProperties() {
        return new HashMap<>();
    }

    //由于两个dataSource的Bean和MapperAutoConfiguration有冲突
    //这里使用Map的形式来注入druidDataSourceProperties
    @Primary
    @Bean("dataSource")
    public DataSourceProxy dataSource(@Qualifier("druidDataSourceProperties") Map<String, String> druidDataSourceProperties) throws InvocationTargetException, IllegalAccessException {
        DruidDataSource druidDataSource = new DruidDataSource();
        BeanUtils.copyProperties(dataSourceProperties, druidDataSource);
        org.apache.commons.beanutils.BeanUtils.populate(druidDataSource, druidDataSourceProperties);
        return new DataSourceProxy(druidDataSource);
    }

}
