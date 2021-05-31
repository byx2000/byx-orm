package byx.orm.core;

import java.lang.reflect.Method;

/**
 * 从Dao方法生成sql字符串
 * 用户可自定义此接口的实现类来扩展ByxOrm的功能
 *
 * @author byx
 */
public interface SqlGenerator {
    /**
     * 是否支持当前方法
     * @param method 方法
     * @param params 方法参数
     * @return 是否支持
     */
    boolean support(Method method, Object[] params);

    /**
     * 获取sql字符串
     * @param method 方法
     * @param params 方法参数
     * @return sql字符串
     */
    String getSql(Method method, Object[] params);
}
