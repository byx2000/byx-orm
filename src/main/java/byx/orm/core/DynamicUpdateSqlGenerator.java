package byx.orm.core;

import byx.orm.annotation.DynamicUpdate;
import byx.orm.util.PlaceholderUtils;

import java.lang.reflect.Method;

/**
 * 根据DynamicUpdate注解生成sql字符串
 */
public class DynamicUpdateSqlGenerator extends DynamicSqlGenerator {
    @Override
    public boolean support(MethodContext ctx) {
        return ctx.getMethod().isAnnotationPresent(DynamicUpdate.class);
    }

    @Override
    public String getSql(MethodContext ctx) {
        String sqlTemplate = getDynamicUpdateSql(ctx.getMethod(), ctx.getArgs());
        return PlaceholderUtils.replace(sqlTemplate, ctx.getArgsMap());
    }

    private String getDynamicUpdateSql(Method method, Object[] params) {
        DynamicUpdate dua = method.getAnnotation(DynamicUpdate.class);
        Class<?> type = dua.type();
        String methodName = "".equals(dua.method()) ? method.getName() : dua.method();
        return getDynamicSql(type, methodName, method, params);
    }
}
