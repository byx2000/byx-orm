package byx.orm.core;

import byx.orm.annotation.DynamicUpdate;
import byx.orm.util.PlaceholderUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 从DynamicUpdate注解生成Sql字符串
 *
 * @author byx
 */
public class DynamicUpdateAnnotationSqlGenerator extends SqlGeneratorSupport implements SqlGenerator {
    @Override
    public boolean support(Method method, Object[] params) {
        return method.isAnnotationPresent(DynamicUpdate.class);
    }

    @Override
    public String getSql(Method method, Object[] params) {
        String sqlTemplate = getDynamicUpdateSql(method, params);
        Map<String, Object> paramMap = getParamMap(method, params);
        return PlaceholderUtils.replace(sqlTemplate, paramMap);
    }

    @Override
    public SqlType getType() {
        return SqlType.UPDATE;
    }
}
