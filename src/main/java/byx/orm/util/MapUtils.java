package byx.orm.util;

import byx.orm.annotation.Column;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 映射工具类
 *
 * @author byx
 */
public class MapUtils {
    /**
     * 将Map转换为实体类
     * @param resultType 实体类类型
     * @param resultMap 结果集
     * @return 实体类实例
     */
    public static Object mapToObject(Class<?> resultType, Map<String, Object> resultMap) {
        try {
            Constructor<?> constructor = resultType.getConstructor();
            constructor.setAccessible(true);
            Object result = constructor.newInstance();
            for (Field field : resultType.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class)) {
                    field.set(result, resultMap.get(field.getAnnotation(Column.class).value()));
                } else {
                    field.set(result, resultMap.get(field.getName()));
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
