package byx.orm.core;

import byx.orm.annotation.Query;
import byx.orm.util.PlaceholderUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 从Query注解生成sql字符串
 *
 * @author byx
 */
public class QueryAnnotationSqlGenerator extends SqlGeneratorSupport implements SqlGenerator {
    @Override
    public boolean support(Method method, Object[] params) {
        return method.isAnnotationPresent(Query.class);
    }

    @Override
    public String getSql(Method method, Object[] params) {
        String sqlTemplate = method.getAnnotation(Query.class).value();
        Map<String, Object> paramMap = getParamMap(method, params);
        return PlaceholderUtils.replace(sqlTemplate, paramMap);
    }

    @Override
    public SqlType getType() {
        return SqlType.QUERY;
    }
}
