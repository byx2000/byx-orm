package byx.orm.core;

import byx.orm.annotation.Column;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 对象映射器
 *
 * @author byx
 */
public class ObjectMapper {
    /**
     * 将结果集的一行转换成对象
     * @param resultType 结果类型
     * @param rowMap 一行数据
     * @return 对象
     */
    public static Object mapToObject(Class<?> resultType, Map<String, Object> rowMap) {
        if (resultType.isPrimitive() || resultType == Integer.class ||
            resultType == Double.class || resultType == String.class) {
            return mapToPrimitive(rowMap);
        } else {
            return mapToJavaBean(resultType, rowMap);
        }
    }

    private static Object mapToPrimitive(Map<String, Object> rowMap) {
        if (rowMap.isEmpty()) {
            throw new RuntimeException("结果集为空");
        }
        if (rowMap.size() > 1) {
            throw new RuntimeException("列数大于1");
        }

        return rowMap.values().toArray()[0];
    }

    private static Object mapToJavaBean(Class<?> resultType, Map<String, Object> rowMap) {
        try {
            Constructor<?> constructor = resultType.getConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            for (Field field : resultType.getDeclaredFields()) {
                String column = field.isAnnotationPresent(Column.class)
                        ? field.getAnnotation(Column.class).value()
                        : field.getName();
                if (rowMap.containsKey(column)) {
                    field.setAccessible(true);
                    field.set(instance, rowMap.get(column));
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
