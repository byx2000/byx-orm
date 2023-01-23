package byx.orm.core;

import byx.orm.exception.ByxOrmException;
import byx.orm.util.jdbc.JdbcUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Dao生成器
 */
public class DaoGenerator {
    private List<SqlGenerator> sqlGenerators = new ArrayList<>();
    private List<SqlExecutor> sqlExecutors = new ArrayList<>();

    /**
     * 创建DaoGenerator
     * @param dataSource 数据源
     */
    public DaoGenerator(DataSource dataSource) {
        this(new JdbcUtils(dataSource));
    }

    /**
     * 创建DaoGenerator
     * @param jdbcUtils jdbc工具类
     */
    public DaoGenerator(JdbcUtils jdbcUtils) {
        sqlGenerators.add(new QuerySqlGenerator());
        sqlGenerators.add(new UpdateSqlGenerator());
        sqlGenerators.add(new DynamicQuerySqlGenerator());
        sqlGenerators.add(new DynamicUpdateSqlGenerator());
        sqlExecutors.add(new DefaultSqlExecutor(jdbcUtils));
    }

    /**
     * 设置SqlGenerator列表
     * @param sqlGenerators SqlGenerator列表
     */
    public void setSqlGenerators(List<SqlGenerator> sqlGenerators) {
        this.sqlGenerators = sqlGenerators;
    }

    /**
     * 添加自定义SqlGenerator
     * @param sqlGenerator SqlGenerator实现类
     */
    public void addSqlGenerator(SqlGenerator sqlGenerator) {
        sqlGenerators.add(sqlGenerator);
    }

    /**
     * 设置SqlExecutor列表
     * @param sqlExecutors SqlExecutor列表
     */
    public void setSqlExecutors(List<SqlExecutor> sqlExecutors) {
        this.sqlExecutors = sqlExecutors;
    }

    /**
     * 添加自定义SqlExecutor
     * @param sqlExecutor SqlExecutor实现类
     */
    public void addSqlExecutor(SqlExecutor sqlExecutor) {
        sqlExecutors.add(0, sqlExecutor);
    }

    /**
     * 生成Dao接口的实现类
     * @param daoInterface Dao接口类型
     * @param <T> 接口类型
     * @return 接口实现类
     */
    @SuppressWarnings("unchecked")
    public <T> T generate(Class<T> daoInterface) {
        return (T) Proxy.newProxyInstance(DaoGenerator.class.getClassLoader(),
                new Class<?>[]{daoInterface},
                new DaoInvocationHandler());
    }

    // Dao方法拦截器
    private class DaoInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else {
                MethodContext ctx = new MethodContext(method, args);

                // 生成sql语句
                SqlGenerator sqlGenerator = searchSqlGenerator(ctx);
                if (sqlGenerator == null) {
                    throw new ByxOrmException("SqlGenerator not found: " + ctx);
                }
                String sql = sqlGenerator.getSql(ctx);

                // 执行sql语句并返回结果
                SqlExecutor sqlExecutor = searchSqlExecutor(ctx);
                if (sqlExecutor == null) {
                    throw new ByxOrmException("SqlExecutor not found: " + ctx);
                }
                return sqlExecutor.execute(ctx, sql);
            }
        }
    }

    // 查找SqlGenerator
    private SqlGenerator searchSqlGenerator(MethodContext ctx) {
        for (SqlGenerator g : sqlGenerators) {
            if (g.support(ctx)) {
                return g;
            }
        }
        return null;
    }

    // 查找SqlExecutor
    private SqlExecutor searchSqlExecutor(MethodContext ctx) {
        for (SqlExecutor e : sqlExecutors) {
            if (e.support(ctx)) {
                return e;
            }
        }
        return null;
    }
}
