package byx.orm.util;

import java.util.List;

/**
 * 字符串工具类
 *
 * @author byx
 */
public class StringUtils {
    /**
     * 拼接字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @param delimiter 分隔符
     * @param strings 字符串列表
     * @return 拼接后的字符串
     */
    public static String concat(String prefix, String suffix, String delimiter, List<String> strings) {
        return prefix + String.join(delimiter, strings) + suffix;
    }
}
