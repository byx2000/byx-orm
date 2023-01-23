package byx.orm.core;

import byx.orm.annotation.DynamicSql;
import byx.orm.exception.ByxOrmException;
import byx.orm.util.PlaceholderUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 根据DynamicSql注解生成sql字符串
 */
public class DynamicSqlGenerator implements SqlGenerator {
    @Override
    public boolean support(MethodContext ctx) {
        return ctx.getMethod().isAnnotationPresent(DynamicSql.class);
    }

    @Override
    public String getSql(MethodContext ctx) {
        String sqlTemplate = getDynamicSql(ctx.getMethod(), ctx.getArgs());
        return PlaceholderUtils.replace(sqlTemplate, ctx.getArgsMap());
    }

    private String getDynamicSql(Method method, Object[] params) {
        DynamicSql dqa = method.getAnnotation(DynamicSql.class);
        Class<?> type = dqa.type();
        String methodName = "".equals(dqa.method()) ? method.getName() : dqa.method();
        return getDynamicSqlFromClass(type, methodName, method, params);
    }

    private String getDynamicSqlFromClass(Class<?> type, String methodName, Method daoMethod, Object[] params) {
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
