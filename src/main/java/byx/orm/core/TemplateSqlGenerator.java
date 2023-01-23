package byx.orm.core;

import byx.orm.annotation.Sql;
import byx.orm.util.PlaceholderUtils;

/**
 * 根据Sql注解生成sql字符串
 */
public class TemplateSqlGenerator implements SqlGenerator {
    @Override
    public boolean support(MethodContext ctx) {
        return ctx.getMethod().isAnnotationPresent(Sql.class);
    }

    @Override
    public String getSql(MethodContext ctx) {
        String sqlTemplate = ctx.getMethod().getAnnotation(Sql.class).value();
        return PlaceholderUtils.replace(sqlTemplate, ctx.getArgsMap());
    }
}
