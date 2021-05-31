package byx.orm.core;

import byx.orm.annotation.DynamicQuery;
import byx.orm.util.PlaceholderUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 从DynamicQuery注解生成sql字符串
 *
 * @author byx
 */
public class DynamicQueryAnnotationSqlGenerator extends SqlGeneratorSupport implements SqlGenerator {
    @Override
    public boolean support(Method method, Object[] params) {
        return method.isAnnotationPresent(DynamicQuery.class);
    }

    @Override
    public String getSql(Method method, Object[] params) {
        String sqlTemplate = getDynamicQuerySql(method, params);
        Map<String, Object> paramMap = getParamMap(method, params);
        return PlaceholderUtils.replace(sqlTemplate, paramMap);
    }
}
