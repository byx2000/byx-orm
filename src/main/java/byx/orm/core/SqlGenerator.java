package byx.orm.core;

/**
 * 根据Dao方法生成sql字符串，用户可自定义此接口的实现类来扩展ByxOrm的功能
 */
public interface SqlGenerator {
    /**
     * 是否支持当前方法
     * @param ctx 上下文
     * @return 是否支持
     */
    boolean support(MethodContext ctx);

    /**
     * 获取sql字符串
     * @param ctx 上下文
     * @return sql字符串
     */
    String getSql(MethodContext ctx);
}
