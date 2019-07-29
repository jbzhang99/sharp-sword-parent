package com.dili.ss.dto;

import javassist.CtClass;
import org.reflections.Reflections;
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
        Set<String> basePackages = getBasePackages(annotationMetadata);
        Reflections reflections = new Reflections(basePackages);
        Set<Class<? extends IDTO>> classes = reflections.getSubTypesOf(IDTO.class);
        if (classes != null) {
            for (Class<? extends IDTO> dtoClass : classes) {
                if(!dtoClass.isInterface()){
                    continue;
                }
                try {
                if(IBaseDomain.class.isAssignableFrom(dtoClass)){
                    DTOFactory.createBaseDomainCtClass((Class<IBaseDomain>)dtoClass);
                }else{
                    DTOFactory.createDTOCtClass(dtoClass);
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(dtoClass);
            }
        }

    }

    public static void main(String[] args) throws Exception {
        CtClass ctClass = DTOFactory.createBaseDomainCtClass(IBaseDomain.class);
        ctClass.toClass();
        Class<IBaseDomain> clazz = (Class)Class.forName(IBaseDomain.class.getName()+DTOFactory.INSTANCE_SUFFIX);
        IBaseDomain iBaseDomain = clazz.newInstance();
        iBaseDomain.setId(9L);
        System.out.println(iBaseDomain.getId());
//        Set<String> basePackages = Sets.newHashSet("com.dili");
//        Reflections reflections = new Reflections(new ConfigurationBuilder()
//                .filterInputsBy(new FilterBuilder().includePackage(basePackages.toArray(new String[]{})))
//                .setUrls(ClasspathHelper.forClass(IDTO.class))
//                .setScanners(new SubTypesScanner()));
//        Set<Class<? extends IDTO>> classes = reflections.getSubTypesOf(IDTO.class);
//        if (classes != null) {
//            for (Class<? extends IDTO> dto : classes) {
//                System.out.println(dto);
//            }
//        }
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
