package byx.orm.core;

import byx.orm.annotation.*;
import byx.orm.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Sql字符串生成器
 *
 * @author byx
 */
public class SqlGenerator {
    public static String getSql(Object obj) {
        Class<?> type = obj.getClass();
        Map<String, Object> paramMap = getParamMap(obj);

        if (type.isAnnotationPresent(Sql.class)) {
            String sql = type.getAnnotation(Sql.class).value();
            return PlaceholderProcessor.replace(sql, paramMap);
        } else {
            String prefix = PlaceholderProcessor.replace(getPrefixExpr(type), paramMap);
            String suffix = PlaceholderProcessor.replace(getSuffixExpr(type), paramMap);
            String delimiter = PlaceholderProcessor.replace(getDelimiterExpr(type), paramMap);
            List<String> strs = getListExpr(obj).stream()
                    .map(s -> PlaceholderProcessor.replace(s, paramMap))
                    .collect(Collectors.toList());
            return StringUtils.concat(prefix, suffix, delimiter, strs);
        }


    }

    private static Map<String, Object> getParamMap(Object obj) {
        try {
            Map<String, Object> paramMap = new HashMap<>();
            for (Field field : obj.getClass().getDeclaredFields()) {
                paramMap.put(field.getName(), getFieldValue(obj, field));
            }
            return paramMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getPrefixExpr(Class<?> type) {
        if (type.isAnnotationPresent(Prefix.class)) {
            return type.getAnnotation(Prefix.class).value();
        }
        return "";
    }

    private static String getSuffixExpr(Class<?> type) {
        if (type.isAnnotationPresent(Suffix.class)) {
            return type.getAnnotation(Suffix.class).value();
        }
        return "";
    }

    private static String getDelimiterExpr(Class<?> type) {
        if (type.isAnnotationPresent(Delimiter.class)) {
            return type.getAnnotation(Delimiter.class).value();
        }
        return "";
    }

    private static List<Field> getFields(Class<?> type, String[] params) {
        try {
            List<Field> fields = new ArrayList<>();
            for (String p : params) {
                fields.add(type.getDeclaredField(p));
            }
            return fields;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getListExpr(Object obj) {
        try {
            List<String> strs = new ArrayList<>();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(obj) != null) {
                    if (field.isAnnotationPresent(Sql.class)) {
                        strs.add(field.getAnnotation(Sql.class).value());
                    }
                }
            }
            return strs;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getFieldValue(Object obj, Field field) {
        try {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Convert.class)) {
                Convert a = field.getAnnotation(Convert.class);
                Class<?> provider = a.type();
                String method = "".equals(a.method()) ? field.getName() : a.method();
                String[] params = a.params();

                List<Field> fields;
                if (params.length == 0) {
                    fields = Collections.singletonList(field);
                } else {
                    fields = getFields(obj.getClass(), params);
                }

                Constructor<?> constructor = provider.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object instance = constructor.newInstance();
                Method m = provider.getMethod(method, fields.stream().map(Field::getType).toArray(Class<?>[]::new));
                m.setAccessible(true);
                return m.invoke(instance, fields.stream().map(f -> {
                    try {
                        f.setAccessible(true);
                        return f.get(obj);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray());
            } else {
                return field.get(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
