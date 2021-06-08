package byx.orm.core;

import byx.orm.annotation.Update;
import byx.orm.util.PlaceholderUtils;

import java.util.Map;

/**
 * 从Update注解生成sql字符串
 *
 * @author byx
 */
public class UpdateAnnotationSqlGenerator extends SqlGeneratorSupport implements SqlGenerator {
    @Override
    public boolean support(MethodContext ctx) {
        return ctx.getMethod().isAnnotationPresent(Update.class);
    }

    @Override
    public String getSql(MethodContext ctx) {
        String sqlTemplate = ctx.getMethod().getAnnotation(Update.class).value();
        Map<String, Object> paramMap = getParamMap(ctx.getMethod(), ctx.getArgs());
        return PlaceholderUtils.replace(sqlTemplate, paramMap);
    }

}
