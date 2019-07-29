package com.dili.ss.dto;

import javassist.*;

/**
 * DTO工厂
 */
public class DTOFactory {

    static ClassPool classPool = ClassPool.getDefault();

    public static final String INSTANCE_SUFFIX = "$Impl";

    static {
        classPool.insertClassPath(new ClassClassPath(DTOFactory.class));
    }

    public static <T extends IDTO> CtClass createCtClass(Class<T> clazz) throws Exception {
        CtClass ctClass = null;
        if(IBaseDomain.class.isAssignableFrom(clazz)){
            ctClass = createBaseDomainCtClass((Class<IBaseDomain>)clazz);
        }else{
            ctClass = createDTOCtClass(clazz);
        }
        return null;
    }

    public static <T extends IDTO> CtClass createDTOCtClass(Class<T> clazz) throws Exception {
        CtClass intfCtClass = classPool.get(clazz.getName());
        CtClass implCtClass = classPool.makeClass(clazz.getName() + INSTANCE_SUFFIX);
        implCtClass.setInterfaces(new CtClass[]{intfCtClass});
        CtField $delegateField = CtField.make("private com.dili.ss.dto.DTO $delegate;", implCtClass);
        implCtClass.addField($delegateField);
        CtConstructor defaultConstructor = new CtConstructor(null, implCtClass);
        defaultConstructor.setModifiers(Modifier.PUBLIC);
        defaultConstructor.setBody("{this.$delegate = new com.dili.ss.dto.DTO();}");
        implCtClass.addConstructor(defaultConstructor);

        CtMethod agetMethod = CtMethod.make("public Object aget(String property){return $delegate.get(property);}", implCtClass);
        CtMethod asetMethod = CtMethod.make("public void aset(String property, Object value){this.$delegate.put(property, value);}", implCtClass);
        implCtClass.addMethod(agetMethod);
        implCtClass.addMethod(asetMethod);

        CtMethod mgetMethod = CtMethod.make("public Object mget(String property){return this.$delegate.getMetadata(property);}", implCtClass);
        CtMethod mgetAllMethod = CtMethod.make("public java.util.Map mget(){return this.$delegate;}", implCtClass);
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

        CtMethod getIdMethod = CtMethod.make("public Long getId(){return id;}", implCtClass);
        CtMethod setIdMethod = CtMethod.make("public void setId(Long id){this.id=id;}", implCtClass);

        CtMethod getPageMethod = CtMethod.make("public Integer getPage(){return page;}", implCtClass);
        CtMethod setPageMethod = CtMethod.make("public void setPage(Integer page){this.page=page;}", implCtClass);

        CtMethod getRowsMethod = CtMethod.make("public Integer getRows(){return rows;}", implCtClass);
        CtMethod setRowsMethod = CtMethod.make("public void setRows(Integer rows){this.rows=rows;}", implCtClass);

        CtMethod getSortMethod = CtMethod.make("public String getSort(){return sort;}", implCtClass);
        CtMethod setSortMethod = CtMethod.make("public void setSort(String sort){this.sort=sort;}", implCtClass);

        CtMethod getOrderMethod = CtMethod.make("public String getOrder(){return order;}", implCtClass);
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
