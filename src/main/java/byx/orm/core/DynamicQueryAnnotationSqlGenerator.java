package byx.orm.core;

import byx.orm.annotation.DynamicQuery;
import byx.orm.util.PlaceholderUtils;

import java.util.Map;

/**
 * 从DynamicQuery注解生成sql字符串
 *
 * @author byx
 */
public class DynamicQueryAnnotationSqlGenerator extends SqlGeneratorSupport implements SqlGenerator {
    @Override
    public boolean support(MethodContext ctx) {
        return ctx.getMethod().isAnnotationPresent(DynamicQuery.class);
    }

    @Override
    public String getSql(MethodContext ctx) {
        String sqlTemplate = getDynamicQuerySql(ctx.getMethod(), ctx.getArgs());
        Map<String, Object> paramMap = getParamMap(ctx.getMethod(), ctx.getArgs());
        return PlaceholderUtils.replace(sqlTemplate, paramMap);
    }

}
