package byx.orm.core;

import byx.orm.annotation.Update;
import byx.orm.util.PlaceholderUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 从Update注解生成sql字符串
 *
 * @author byx
 */
public class UpdateAnnotationSqlGenerator extends SqlGeneratorSupport implements SqlGenerator {
    @Override
    public boolean support(Method method, Object[] params) {
        return method.isAnnotationPresent(Update.class);
    }

    @Override
    public String getSql(Method method, Object[] params) {
        String sqlTemplate = method.getAnnotation(Update.class).value();
        Map<String, Object> paramMap = getParamMap(method, params);
        return PlaceholderUtils.replace(sqlTemplate, paramMap);
    }
}
