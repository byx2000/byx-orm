package byx.orm.util;

import byx.orm.exception.ByxOrmException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 占位符工具类
 * 支持#{...}和${...}两种占位符
 * 具体含义与MyBatis中的占位符相同
 *
 * @author byx
 */
public class PlaceholderUtils {
    /**
     * 匹配#{...}或${...}的占位符表达式
     */
    private static final Pattern PATTERN = Pattern.compile("[#$]\\{([a-zA-Z]|_)([0-9a-zA-Z]|_|\\.)*}");

    /**
     * 替换字符串中的占位符
     * @param str 带占位符的sql字符串
     * @param paramMap 参数表
     * @return 不带占位符的sql字符串
     */
    public static String replace(String str, Map<String, Object> paramMap) {
        List<String> placeholders = getPlaceholders(str);
        for (String p : placeholders) {
            String key = p.substring(2, p.length() - 1);
            Object value = getPlaceholderValue(paramMap, key);
            if (p.startsWith("#")) {
                str = str.replace(p, getValueString(value));
            } else if (p.startsWith("$")) {
                str = str.replace(p, value.toString());
            }
        }
        return str;
    }

    /**
     * 获取字符串中的所有占位符
     */
    private static List<String> getPlaceholders(String str) {
        List<String> result = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(str);
        while (matcher.find()) {
            String s = matcher.group();
            result.add(s);
        }
        return result;
    }

    /**
     * 计算占位符表达式的值
     */
    private static Object getPlaceholderValue(Map<String, Object> paramMap, String expr) {
        String[] path = expr.split("\\.");
        if (!paramMap.containsKey(path[0])) {
            throw new ByxOrmException("The key " + path[0] + " is not exist in parameter map: " + paramMap);
        }
        Object obj = paramMap.get(path[0]);
        return getValue(obj, path, 1);
    }

    private static Object getValue(Object obj, String[] path, int index) {
        if (index == path.length) {
            return obj;
        }

        try {
            PropertyDescriptor pd = new PropertyDescriptor(path[index], obj.getClass());
            Method getter = pd.getReadMethod();
            getter.setAccessible(true);
            return getValue(getter.invoke(obj), path, index + 1);
        } catch (Exception e) {
            throw new ByxOrmException("Error when get property " + path[index] + " of " + obj.getClass(), e);
        }
    }

    /**
     * 将Java中的对象转换为Sql中的字符串
     */
    private static String getValueString(Object obj) {
        Class<?> type = obj.getClass();
        if (type == String.class || type == Character.class) {
            return "'" + obj + "'";
        } else {
            return obj.toString();
        }
    }
}
