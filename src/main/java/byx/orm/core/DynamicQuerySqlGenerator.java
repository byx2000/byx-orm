package byx.orm.core;

import byx.orm.annotation.DynamicQuery;
import byx.orm.util.PlaceholderUtils;

import java.lang.reflect.Method;

/**
 * 从DynamicQuery注解生成sql字符串
 *
 * @author byx
 */
public class DynamicQuerySqlGenerator extends DynamicSqlGenerator {
    @Override
    public boolean support(MethodContext ctx) {
        return ctx.getMethod().isAnnotationPresent(DynamicQuery.class);
    }

    @Override
    public String getSql(MethodContext ctx) {
        String sqlTemplate = getDynamicQuerySql(ctx.getMethod(), ctx.getArgs());
        return PlaceholderUtils.replace(sqlTemplate, ctx.getArgsMap());
    }

    private String getDynamicQuerySql(Method method, Object[] params) {
        DynamicQuery dqa = method.getAnnotation(DynamicQuery.class);
        Class<?> type = dqa.type();
        String methodName = "".equals(dqa.method()) ? method.getName() : dqa.method();
        return getDynamicSql(type, methodName, method, params);
    }
}
