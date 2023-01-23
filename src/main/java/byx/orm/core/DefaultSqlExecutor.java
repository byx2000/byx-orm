package byx.orm.core;

import byx.orm.exception.ByxOrmException;
import byx.orm.util.ObjectMapper;
import byx.orm.util.jdbc.JdbcUtils;
import byx.orm.util.jdbc.MapRowMapper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认的SqlExecutor实现类
 * 自动判断sql语句类型（查询或更新）
 * 根据方法返回值类型返回不同的结果
 *
 * @author byx
 */
public class DefaultSqlExecutor implements SqlExecutor {
    private final JdbcUtils jdbcUtils;

    private static final String QUERY_PREFIX = "SELECT";

    public DefaultSqlExecutor(JdbcUtils jdbcUtils) {
        this.jdbcUtils = jdbcUtils;
    }

    @Override
    public boolean support(MethodContext ctx) {
        return true;
    }

    @Override
    public Object execute(MethodContext ctx, String sql) {
        if (sql.trim().toUpperCase(Locale.ROOT).startsWith(QUERY_PREFIX)) {
            return executeQuery(sql, ctx.getMethod());
        } else {
            return executeUpdate(sql, ctx.getMethod());
        }
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
