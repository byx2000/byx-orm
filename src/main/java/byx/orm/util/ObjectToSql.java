package byx.orm.util;

import byx.orm.annotation.Delimiter;
import byx.orm.annotation.Prefix;
import byx.orm.annotation.Sql;
import byx.orm.annotation.Suffix;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 将对象转换为sql字符串
 *
 * @author byx
 */
public class ObjectToSql {
    /**
     * 生成对象对应的sql字符串
     * @param obj 对象
     * @return sql字符串
     */
    public static String generate(Object obj) {
        Class<?> type = obj.getClass();

        Map<String, Object> paramMap = getParamMap(obj);

        if (type.isAnnotationPresent(Sql.class)) {
            String sql = type.getAnnotation(Sql.class).value();
            return PlaceholderUtils.replace(sql, paramMap);
        } else {
            String prefix = getPrefix(obj, paramMap);
            String suffix = getSuffix(obj, paramMap);
            String delimiter = getDelimiter(obj, paramMap);
            List<String> strings = getSqlTemplates(obj).stream()
                    .map(s -> PlaceholderUtils.replace(s, paramMap))
                    .collect(Collectors.toList());
            return StringUtils.concat(prefix, suffix, delimiter, strings);
        }
    }

    /**
     * 获取参数表
     */
    private static Map<String, Object> getParamMap(Object obj) {
        try {
            Map<String, Object> paramMap = new HashMap<>(10);
            Class<?> type = obj.getClass();

            Arrays.stream(Introspector.getBeanInfo(type).getPropertyDescriptors()).forEach(d -> {
                try {
                    Method reader = d.getReadMethod();
                    if (reader != null) {
                        reader.setAccessible(true);
                        paramMap.put(d.getName(), reader.invoke(obj));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            return paramMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取前缀
     */
    private static String getPrefix(Object obj, Map<String, Object> paramMap) {
        Class<?> type = obj.getClass();
        if (type.isAnnotationPresent(Prefix.class)) {
            return PlaceholderUtils.replace(type.getAnnotation(Prefix.class).value(), paramMap);
        }
        return "";
    }

    /**
     * 获取后缀
     */
    private static String getSuffix(Object obj, Map<String, Object> paramMap) {
        Class<?> type = obj.getClass();
        if (type.isAnnotationPresent(Suffix.class)) {
            return PlaceholderUtils.replace(type.getAnnotation(Suffix.class).value(), paramMap);
        }
        return "";
    }

    /**
     *
     * 获取分隔符
     */
    private static String getDelimiter(Object obj, Map<String, Object> paramMap) {
        Class<?> type = obj.getClass();
        if (type.isAnnotationPresent(Delimiter.class)) {
            return PlaceholderUtils.replace(type.getAnnotation(Delimiter.class).value(), paramMap);
        }
        return "";
    }

    /**
     * 获取待处理的sql模板列表
     */
    private static List<String> getSqlTemplates(Object obj) {
        List<String> templates = new ArrayList<>();
        Class<?> type = obj.getClass();
        Arrays.stream(type.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Sql.class)).forEach(f -> {
            try {
                f.setAccessible(true);
                if (f.get(obj) != null) {
                    templates.add(f.getAnnotation(Sql.class).value());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Arrays.stream(type.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Sql.class)).forEach(m -> {
            try {
                m.setAccessible(true);
                if (m.invoke(obj) != null) {
                    templates.add(m.getAnnotation(Sql.class).value());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return templates;
    }
}
