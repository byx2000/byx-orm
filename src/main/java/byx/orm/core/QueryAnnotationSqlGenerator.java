package byx.orm.core;

import byx.orm.annotation.Query;
import byx.orm.util.PlaceholderUtils;

import java.util.Map;

/**
 * 从Query注解生成sql字符串
 *
 * @author byx
 */
public class QueryAnnotationSqlGenerator extends SqlGeneratorSupport implements SqlGenerator {
    @Override
    public boolean support(MethodContext ctx) {
        return ctx.getMethod().isAnnotationPresent(Query.class);
    }

    @Override
    public String getSql(MethodContext ctx) {
        String sqlTemplate = ctx.getMethod().getAnnotation(Query.class).value();
        Map<String, Object> paramMap = getParamMap(ctx.getMethod(), ctx.getArgs());
        return PlaceholderUtils.replace(sqlTemplate, paramMap);
    }

    @Override
    public SqlType getType(MethodContext ctx) {
        return SqlType.QUERY;
    }
}
