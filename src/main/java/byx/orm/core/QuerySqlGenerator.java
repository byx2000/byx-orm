package byx.orm.core;

import byx.orm.annotation.Query;
import byx.orm.util.PlaceholderUtils;

/**
 * 从Query注解生成sql字符串
 *
 * @author byx
 */
public class QuerySqlGenerator implements SqlGenerator {
    @Override
    public boolean support(MethodContext ctx) {
        return ctx.getMethod().isAnnotationPresent(Query.class);
    }

    @Override
    public String getSql(MethodContext ctx) {
        String sqlTemplate = ctx.getMethod().getAnnotation(Query.class).value();
        return PlaceholderUtils.replace(sqlTemplate, ctx.getArgsMap());
    }

}
