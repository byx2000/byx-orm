package byx.orm.core;

import byx.orm.annotation.DynamicUpdate;
import byx.orm.util.PlaceholderUtils;

import java.util.Map;

/**
 * 从DynamicUpdate注解生成Sql字符串
 *
 * @author byx
 */
public class DynamicUpdateAnnotationSqlGenerator extends SqlGeneratorSupport implements SqlGenerator {
    @Override
    public boolean support(MethodContext ctx) {
        return ctx.getMethod().isAnnotationPresent(DynamicUpdate.class);
    }

    @Override
    public String getSql(MethodContext ctx) {
        String sqlTemplate = getDynamicUpdateSql(ctx.getMethod(), ctx.getArgs());
        Map<String, Object> paramMap = getParamMap(ctx.getMethod(), ctx.getArgs());
        return PlaceholderUtils.replace(sqlTemplate, paramMap);
    }

}
