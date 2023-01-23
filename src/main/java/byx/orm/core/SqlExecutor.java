package byx.orm.core;

/**
 * sql执行器
 */
public interface SqlExecutor {
    /**
     * 是否支持当前方法
     * @param ctx 上下文
     * @return 是否支持
     */
    boolean support(MethodContext ctx);

    /**
     * 执行sql语句，并返回执行结果
     * @param ctx 上下文
     * @param sql sql字符串
     * @return 执行结果
     */
    Object execute(MethodContext ctx, String sql);
}
