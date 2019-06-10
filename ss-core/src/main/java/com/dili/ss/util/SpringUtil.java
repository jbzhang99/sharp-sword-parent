package com.dili.ss.util;

/**
 * Created by asiamastor on 2017/1/22.
 */

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //获取applicationContext

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
        System.out.println("---------------SpringUtil------------------------------------------------------");
        System.out.println("ApplicationContext配置成功,在普通类可以通过调用SpringUtils.getAppContext()获取applicationContext对象,applicationContext=" + SpringUtil.applicationContext);
        System.out.println("--------------------------------------------------------------------------------");
    }


    //通过name获取 Bean.
    public static Object getBean(String name) {
        if(getApplicationContext() != null) {
            return getApplicationContext().getBean(name);
        }else{
            return null;
        }
    }

    /**
     * 获取某一类的所有的bean
     * includeNonSingletons和allowEagerInit默认为true
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz){
        return getApplicationContext().getBeansOfType(clazz);
    }

    /**
     * 获取某一类的所有的bean
     * @param clazz
     * @param includeNonSingletons 第一代表是否也包含原型（Class祖先）bean或者或者只是singletons（包含FactoryBean生成的）
     * @param allowEagerInit 第二个表示是否立即实例化懒加载或者由FactoryBean生成的Bean以保证依赖关系
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz, Boolean includeNonSingletons, boolean allowEagerInit){
        return getApplicationContext().getBeansOfType(clazz, includeNonSingletons , allowEagerInit);
    }

    //获取泛型Bean
    public static <T> T getGenericBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }
    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }


    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}