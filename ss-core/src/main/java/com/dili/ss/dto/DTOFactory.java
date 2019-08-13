package com.dili.ss.dto;

import com.dili.ss.exception.ParamErrorException;
import com.dili.ss.util.POJOUtils;
import javassist.*;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * DTO工厂
 */
public class DTOFactory {

    static ClassPool classPool = ClassPool.getDefault();

    static {
        classPool.insertClassPath(new ClassClassPath(DTOFactory.class));
    }

    /**
     * 从指定的包注册DTO实例
     */
    public static void registerDTOInstanceFromPackages(Set<String> packages){
        Reflections reflections = new Reflections(packages);
        Set<Class<? extends IDTO>> classes = reflections.getSubTypesOf(IDTO.class);
        if (classes != null) {
            DTOInstance.useInstance = true;
            for (Class<? extends IDTO> dtoClass : classes) {
                if(!dtoClass.isInterface()){
                    continue;
                }
                try {
                    CtClass ctClass = DTOFactory.createCtClass((Class)dtoClass);
                    DTOInstance.cache.put(dtoClass, (Class)ctClass.toClass());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建DTO接口的CtClass
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T extends IDTO> CtClass createCtClass(Class<T> clazz) throws Exception {
        CtClass ctClass = null;
        if(!clazz.isInterface()){
            throw new ParamErrorException("参数必须是接口");
        }
        if(!IDTO.class.isAssignableFrom(clazz)){
            throw new ParamErrorException("参数必须实现IDTO接口");
        }
        if(IBaseDomain.class.isAssignableFrom(clazz)){
            ctClass = createBaseDomainCtClass((Class<IBaseDomain>)clazz);
        }else{
            ctClass = createDTOCtClass(clazz);
        }
        return createDynaCtClass(clazz, ctClass);
    }

    /**
     * 动态创建CtClass
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T extends IDTO> CtClass createDynaCtClass(Class<T> clazz, CtClass ctClass) throws Exception {
        //IBaseDomain或IDTO已经加载
        if(clazz.equals(IBaseDomain.class) || clazz.equals(IDTO.class)){
            return ctClass;
        }
        //非IDTO子类不加载
        if(!IDTO.class.isAssignableFrom(clazz)){
            return ctClass;
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        if(interfaces != null) {
            for (int i = 0; i < interfaces.length; i++) {
                createDynaCtClass((Class) interfaces[i], ctClass);
            }
        }
        //获取当前类的所有方法
        Method[] declaredMethods = clazz.getDeclaredMethods();
        //先添加Field
        for(Method method : declaredMethods) {
            String fieldName = POJOUtils.getBeanField(method);
            if (POJOUtils.isGetMethod(method)) {
                try{
                    //找不到属性抛异常
                    ctClass.getDeclaredField(fieldName);
                    //找到属性就continue
                    continue;
                }catch (NotFoundException e){
                }
                CtField ctField = CtField.make("private " + method.getReturnType().getTypeName() + " " + fieldName + ";", ctClass);
                ctClass.addField(ctField);
            }
        }
        for(Method method : declaredMethods){
            //方法名不重复
            if(ctClass.getDeclaredMethods(method.getName()).length > 0){
                continue;
            }
            String fieldName = POJOUtils.getBeanField(method);
            if(POJOUtils.isGetMethod(method)){
                //基础类型无法提供代理对象的值，因为无法区分是0还是null
                if(method.getReturnType().isPrimitive()){
                    CtMethod method1 = CtMethod.make("public " + method.getReturnType().getTypeName() + " " + method.getName() + "(){return this." + fieldName + ";}", ctClass);
                    ctClass.addMethod(method1);
                }else {
                    CtMethod method1 = CtMethod.make("public " + method.getReturnType().getTypeName() + " " + method.getName() + "(){return this." + fieldName + " == null ? (" + method.getReturnType().getTypeName() + ")$delegate.get(\"" + fieldName + "\") : this." + fieldName + ";}", ctClass);
                    ctClass.addMethod(method1);
                }

            }else if(POJOUtils.isSetMethod(method)){
                CtMethod method1 = CtMethod.make("public void " + method.getName() + "(" + method.getParameterTypes()[0].getTypeName() + " " + fieldName + "){this." + fieldName + " = " + fieldName + ";}", ctClass);
                ctClass.addMethod(method1);
            }
        }
        return ctClass;

    }
    public static <T extends IDTO> CtClass createDTOCtClass(Class<T> clazz) throws Exception {
        CtClass intfCtClass = classPool.get(clazz.getName());
        CtClass implCtClass = classPool.makeClass(clazz.getName() + DTOInstance.SUFFIX);
        implCtClass.setInterfaces(new CtClass[]{intfCtClass});
        CtField $delegateField = CtField.make("private com.dili.ss.dto.DTO $delegate;", implCtClass);
        implCtClass.addField($delegateField);
        CtConstructor defaultConstructor = new CtConstructor(null, implCtClass);
        defaultConstructor.setModifiers(Modifier.PUBLIC);
        defaultConstructor.setBody("{this.$delegate = new com.dili.ss.dto.DTO();}");
        implCtClass.addConstructor(defaultConstructor);

        CtMethod agetMethod = CtMethod.make("public Object aget(String property){return $delegate.get(property);}", implCtClass);
        CtMethod agetAllMethod = CtMethod.make("public com.dili.ss.dto.DTO aget(){return $delegate;}", implCtClass);
        CtMethod asetMethod = CtMethod.make("public void aset(String property, Object value){this.$delegate.put(property, value);}", implCtClass);
        CtMethod asetAllMethod = CtMethod.make("public void aset(com.dili.ss.dto.DTO dto){this.$delegate = dto;}", implCtClass);
        implCtClass.addMethod(agetMethod);
        implCtClass.addMethod(agetAllMethod);
        implCtClass.addMethod(asetMethod);
        implCtClass.addMethod(asetAllMethod);

        CtMethod mgetMethod = CtMethod.make("public Object mget(String property){return this.$delegate.getMetadata(property);}", implCtClass);
        CtMethod mgetAllMethod = CtMethod.make("public java.util.Map mget(){return this.$delegate.getMetadata();}", implCtClass);
        CtMethod msetMethod = CtMethod.make("public void mset(String property, Object value){this.$delegate.setMetadata(property, value);}", implCtClass);
        CtMethod msetAllMethod = CtMethod.make("public void mset(java.util.Map metadata){this.$delegate.setMetadata(metadata);}", implCtClass);
        implCtClass.addMethod(mgetMethod);
        implCtClass.addMethod(mgetAllMethod);
        implCtClass.addMethod(msetMethod);
        implCtClass.addMethod(msetAllMethod);

        return implCtClass;
    }

    public static <T extends IBaseDomain> CtClass createBaseDomainCtClass(Class<T> clazz) throws Exception {
        CtClass implCtClass = createDTOCtClass(clazz);
//        ctClass.stopPruning(true);
        //创建属性
        CtField idField = CtField.make("private Long id;", implCtClass);
        CtField pageField = CtField.make("private Integer page;", implCtClass);
        CtField rowsField = CtField.make("private Integer rows;", implCtClass);
        CtField sortField = CtField.make("private String sort;", implCtClass);
        CtField orderField = CtField.make("private String order;", implCtClass);

        implCtClass.addField(idField);
        implCtClass.addField(pageField);
        implCtClass.addField(rowsField);
        implCtClass.addField(sortField);
        implCtClass.addField(orderField);

        CtMethod getIdMethod = CtMethod.make("public Long getId(){return id==null?(Long)aget(\"id\"):id;}", implCtClass);
        CtMethod setIdMethod = CtMethod.make("public void setId(Long id){this.id=id;}", implCtClass);

        CtMethod getPageMethod = CtMethod.make("public Integer getPage(){return page==null?(Integer)aget(\"page\"):page;}", implCtClass);
        CtMethod setPageMethod = CtMethod.make("public void setPage(Integer page){this.page=page;}", implCtClass);

        CtMethod getRowsMethod = CtMethod.make("public Integer getRows(){return rows==null?(Integer)aget(\"rows\"):rows;}", implCtClass);
        CtMethod setRowsMethod = CtMethod.make("public void setRows(Integer rows){this.rows=rows;}", implCtClass);

        CtMethod getSortMethod = CtMethod.make("public String getSort(){return sort==null?(String)aget(\"sort\"):sort;}", implCtClass);
        CtMethod setSortMethod = CtMethod.make("public void setSort(String sort){this.sort=sort;}", implCtClass);

        CtMethod getOrderMethod = CtMethod.make("public String getOrder(){return order==null?(String)aget(\"order\"):order;}", implCtClass);
        CtMethod setOrderMethod = CtMethod.make("public void setOrder(String order){this.order=order;}", implCtClass);

        CtMethod getMetadataMethod = CtMethod.make("public java.util.Map getMetadata(){return mget();}", implCtClass);
        CtMethod setMetadataMethod = CtMethod.make("public void setMetadata(java.util.Map metadata){mset(metadata);}", implCtClass);

        CtMethod getMetadata2Method = CtMethod.make("public Object getMetadata(String key){return mget(key);}", implCtClass);
        CtMethod setMetadata2Method = CtMethod.make("public void setMetadata(String key, Object value){mset(key, value);}", implCtClass);

        CtMethod containsMetadataMethod = CtMethod.make("public Boolean containsMetadata(String key){return new Boolean(mget().containsKey(key));}", implCtClass);

        implCtClass.addMethod(getIdMethod);
        implCtClass.addMethod(setIdMethod);
        implCtClass.addMethod(getPageMethod);
        implCtClass.addMethod(setPageMethod);
        implCtClass.addMethod(getRowsMethod);
        implCtClass.addMethod(setRowsMethod);
        implCtClass.addMethod(getSortMethod);
        implCtClass.addMethod(setSortMethod);
        implCtClass.addMethod(getOrderMethod);
        implCtClass.addMethod(setOrderMethod);
        implCtClass.addMethod(getMetadataMethod);
        implCtClass.addMethod(setMetadataMethod);
        implCtClass.addMethod(getMetadata2Method);
        implCtClass.addMethod(setMetadata2Method);
        implCtClass.addMethod(containsMetadataMethod);

        return implCtClass;
    }
}
