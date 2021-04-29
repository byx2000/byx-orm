package byx.orm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 占位符替换工具类
 *
 * @author byx
 */
public class PlaceholderUtils {
    private static final Pattern PATTERN = Pattern.compile("#\\{([a-zA-Z]|_)([0-9a-zA-Z]|_|\\.)*}");

    /**
     * 替换sql中的占位符
     * @param sql 带占位符的sql字符串
     * @param paramMap 参数表
     * @return 不带占位符的sql字符串
     */
    public static String replace(String sql, Map<String, Object> paramMap) {
        List<String> placeholders = getPlaceholders(sql);
        for (String expr : placeholders) {
            Object value = getPlaceholderValue(paramMap, expr);
            sql = sql.replace("#{" + expr + "}", getValueString(value));
        }
        return sql;
    }

    private static List<String> getPlaceholders(String sql) {
        List<String> result = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(sql);
        while (matcher.find()) {
            String s = matcher.group();
            result.add(s.substring(2, s.length() - 1));
        }
        return result;
    }

    private static Object getPlaceholderValue(Map<String, Object> paramMap, String expr) {
        String[] path = expr.split("\\.");
        Object obj = paramMap.get(path[0]);
        return getValue(obj, path, 1);
    }

    private static Object getValue(Object obj, String[] path, int index) {
        if (index == path.length) {
            return obj;
        }
        return getValue(ReflectUtils.getProperty(obj, path[index]), path, index + 1);
    }

    private static String getValueString(Object obj) {
        Class<?> type = obj.getClass();
        if (type == String.class || type == Character.class) {
            return "'" + obj + "'";
        } else {
            return obj.toString();
        }
    }
}
