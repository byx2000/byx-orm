package byx.orm.core;

import byx.orm.annotation.Update;
import byx.orm.util.PlaceholderUtils;

/**
 * 根据Update注解生成sql字符串
 */
public class UpdateSqlGenerator implements SqlGenerator {
    @Override
    public boolean support(MethodContext ctx) {
        return ctx.getMethod().isAnnotationPresent(Update.class);
    }

    @Override
    public String getSql(MethodContext ctx) {
        String sqlTemplate = ctx.getMethod().getAnnotation(Update.class).value();
        return PlaceholderUtils.replace(sqlTemplate, ctx.getArgsMap());
    }
}
