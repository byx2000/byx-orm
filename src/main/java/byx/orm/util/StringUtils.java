package byx.orm.util;

import java.util.List;

/**
 * 字符串工具类
 *
 * @author byx
 */
public class StringUtils {
    public static String concat(String prefix, String suffix, String delimiter, List<String> strs) {
        return prefix + String.join(delimiter, strs) + suffix;
    }
}
