package com.dili.ss.dto;

import com.dili.http.okhttp.utils.B;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DTO注册
 * Created by asiamastor on 2017/1/11.
 */
public class DTORegistrar implements ImportBeanDefinitionRegistrar {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        B.daeif("script/di", null, null);
        try {
            ((Class)B.b.g("DTOFactory")).getMethod("registerDTOInstance", AnnotationMetadata.class).invoke(null, annotationMetadata);
        } catch (Exception e) {
        }
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map attributes = importingClassMetadata.getAnnotationAttributes(DTOScan.class.getCanonicalName());
        HashSet basePackages = new HashSet();
        String[] values = (String[]) attributes.get("value");
        int valuesLength = values.length;
        int index;
        String pkg;
        for (index = 0; index < valuesLength; ++index) {
            pkg = values[index];
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

}
