package com.example.rpc;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class RPCFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaceType;

    public RPCFactoryBean(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        InterfaceProxyHandler handler = new InterfaceProxyHandler(interfaceType);
        //可以定义接口与实际代理对象的映射Map类，使得不同的接口用不同的代理对象(这里所有的接口都是用同一个代理对象InterfaceProxy)
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(),
                new Class[]{interfaceType}, handler);
    }

}
