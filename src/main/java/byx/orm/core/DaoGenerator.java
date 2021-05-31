package byx.orm.core;

import byx.orm.exception.ByxOrmException;
import byx.orm.util.ObjectMapper;
import byx.util.jdbc.JdbcUtils;
import byx.util.jdbc.core.MapRowMapper;

import javax.sql.DataSource;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dao生成器
 *
 * @author byx
 */
public class DaoGenerator {
    private static final String QUERY_PREFIX = "SELECT";

    private final JdbcUtils jdbcUtils;

    private final List<SqlGenerator> sqlGenerators = new ArrayList<>() {
        {
            // 添加自带的SqlGenerator
            add(new QueryAnnotationSqlGenerator());
            add(new UpdateAnnotationSqlGenerator());
            add(new DynamicQueryAnnotationSqlGenerator());
            add(new DynamicUpdateAnnotationSqlGenerator());
            add(new SqlObjectAnnotationSqlGenerator());
        }
    };

    /**
     * 创建DaoGenerator
     * @param dataSource 数据源
     */
    public DaoGenerator(DataSource dataSource) {
        jdbcUtils = new JdbcUtils(dataSource);
    }

    /**
     * 创建DaoGenerator
     * @param jdbcUtils jdbc工具类
     */
    public DaoGenerator(JdbcUtils jdbcUtils) {
        this.jdbcUtils = jdbcUtils;
    }

    /**
     * 添加自定义SqlGenerator
     * @param sqlGenerator SqlGenerator实现类
     */
    public void addSqlGenerator(SqlGenerator sqlGenerator) {
        sqlGenerators.add(sqlGenerator);
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

    /**
     * Dao方法拦截器
     */
    private class DaoInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else {
                String sql = getSql(method, args);
                if (sql.trim().toUpperCase(Locale.ROOT).startsWith(QUERY_PREFIX)) {
                    return executeQuery(sql, method);
                } else {
                    return executeUpdate(sql, method);
                }
            }
        }
    }

    /**
     * 获取sql
     */
    private String getSql(Method method, Object[] params) {
        String sql = null;
        for (SqlGenerator g : sqlGenerators) {
            if (g.support(method, params)) {
                sql = g.getSql(method, params);
                break;
            }
        }

        if (sql == null) {
            throw new ByxOrmException("SqlGenerator not found: " + method);
        }

        return sql;
    }

    /**
     * 执行查询操作并返回结果
     */
    private Object executeQuery(String sql, Method method) {
        System.out.println("sql: " + sql);
        // 执行sql，获取结果集
        List<Map<String, Object>> resultList;
        try {
            resultList = jdbcUtils.queryList(sql, new MapRowMapper());
        } catch (Exception e) {
            throw new ByxOrmException("An error occurred while executing sql: " + sql, e);
        }

        // 获取方法返回值类型
        Class<?> returnType = method.getReturnType();

        // 如果返回值是列表，则获取列表的泛型参数类型，并把结果集的每一行转换成该类型
        // 否则，直接把结果集的第一行转换成返回值类型
        if (returnType == List.class) {
            try {
                Type t = method.getGenericReturnType();
                Class<?> resultType = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
                return resultList.stream().map(m -> ObjectMapper.mapToObject(resultType, m)).collect(Collectors.toList());
            } catch (Exception e) {
                throw new ByxOrmException("Unable to read generic parameters.", e);
            }
        } else {
            if (resultList.isEmpty()) {
                return null;
            }
            if (resultList.size() > 1) {
                throw new ByxOrmException("The number of rows in the result set is greater than 1.");
            }
            return ObjectMapper.mapToObject(returnType, resultList.get(0));
        }
    }

    /**
     * 执行更新操作并返回结果
     */
    private Object executeUpdate(String sql, Method method) {
        System.out.println("sql: " + sql);
        // 如果方法返回值为void，则直接执行更新操作
        // 否则返回影响行数
        try {
            int rows = jdbcUtils.update(sql);
            if (method.getReturnType() != void.class) {
                return rows;
            }
            return null;
        } catch (Exception e) {
            throw new ByxOrmException("An error occurred while executing sql: " + sql, e);
        }
    }
}
