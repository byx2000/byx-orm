package byx.orm.core;

/**
 * Sql执行器
 *
 * @author byx
 */
public interface SqlExecutor {
    /**
     * 是否支持当前方法
     * @param ctx 上下文
     * @return 是否支持
     */
    boolean support(MethodContext ctx);

    /**
     * 执行sql语句
     * @param ctx 上下文
     * @param sql sql字符串
     * @return 执行结果
     */
    Object execute(MethodContext ctx, String sql);
}
