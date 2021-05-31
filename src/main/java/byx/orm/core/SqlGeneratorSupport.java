package byx.orm.core;

import byx.orm.annotation.DynamicQuery;
import byx.orm.annotation.DynamicUpdate;
import byx.orm.exception.ByxOrmException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 为ByxOrm自带的SqlGenerator抽取公共方法
 *
 * @author byx
 */
public class SqlGeneratorSupport {
    /**
     * 将方法参数转换为Map
     */
    protected Map<String, Object> getParamMap(Method method, Object[] params) {
        Parameter[] parameters = method.getParameters();
        String[] paramNames = Arrays.stream(parameters).map(Parameter::getName).toArray(String[]::new);
        Map<String, Object> map = new HashMap<>(10);
        for (int i = 0; i < paramNames.length; ++i) {
            map.put(paramNames[i], params[i]);
        }
        return map;
    }

    /**
     * 获取动态查询sql
     */
    protected String getDynamicQuerySql(Method method, Object[] params) {
        DynamicQuery dqa = method.getAnnotation(DynamicQuery.class);
        Class<?> type = dqa.type();
        String methodName = "".equals(dqa.method()) ? method.getName() : dqa.method();
        return getDynamicSql(type, methodName, method, params);
    }

    /**
     * 获取动态更新sql
     */
    protected String getDynamicUpdateSql(Method method, Object[] params) {
        DynamicUpdate dua = method.getAnnotation(DynamicUpdate.class);
        Class<?> type = dua.type();
        String methodName = "".equals(dua.method()) ? method.getName() : dua.method();
        return getDynamicSql(type, methodName, method, params);
    }

    /**
     * 通过调用方法获取sql字符串
     */
    private String getDynamicSql(Class<?> type, String methodName, Method daoMethod, Object[] params) {
        try {
            Constructor<?> constructor = type.getConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();

            Method method = type.getMethod(methodName, daoMethod.getParameterTypes());
            method.setAccessible(true);
            return (String) method.invoke(instance, params);
        } catch (Exception e) {
            throw new ByxOrmException("Cannot get dynamic sql of: " + daoMethod, e);
        }
    }
}
