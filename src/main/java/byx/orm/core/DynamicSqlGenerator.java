package byx.orm.core;

import byx.orm.exception.ByxOrmException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 动态Sql生成器基类
 *
 * @author byx
 */
public abstract class DynamicSqlGenerator implements SqlGenerator {
    /**
     * 通过调用方法动态获取sql字符串
     */
    protected String getDynamicSql(Class<?> type, String methodName, Method daoMethod, Object[] params) {
        try {
            Constructor<?> constructor = type.getConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();

            Method method = type.getMethod(methodName, daoMethod.getParameterTypes());
            method.setAccessible(true);
            return (String) method.invoke(instance, params);
        } catch (Exception e) {
            throw new ByxOrmException("error when get dynamic sql: " + daoMethod, e);
        }
    }
}
