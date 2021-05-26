package byx.orm;

import byx.orm.annotation.DynamicQuery;
import byx.orm.annotation.DynamicUpdate;
import byx.orm.annotation.Query;
import byx.orm.annotation.Update;
import byx.orm.core.ObjectMapper;
import byx.orm.util.PlaceholderUtils;
import byx.orm.exception.ByxOrmException;
import byx.util.jdbc.JdbcUtils;
import byx.util.jdbc.core.MapRowMapper;

import javax.sql.DataSource;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dao生成器
 *
 * @author byx
 */
public class DaoGenerator {
    private final JdbcUtils jdbcUtils;

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
                // 计算方法参数Map
                Map<String, Object> paramMap = getParamMap(method, args);

                if (method.isAnnotationPresent(Query.class)) {
                    return processQuery(method, paramMap);
                } else if (method.isAnnotationPresent(Update.class)) {
                    return processUpdate(method, paramMap);
                } else if (method.isAnnotationPresent(DynamicQuery.class)) {
                    return processDynamicQuery(method, args);
                } else if (method.isAnnotationPresent(DynamicUpdate.class)) {
                    return processDynamicUpdate(method, args);
                } else {
                    throw new ByxOrmException("Method not implemented: " + method);
                }
            }
        }
    }

    /**
     * 获取参数映射表
     */
    private Map<String, Object> getParamMap(Method method, Object[] params) {
        Parameter[] parameters = method.getParameters();
        String[] paramNames = Arrays.stream(parameters).map(Parameter::getName).toArray(String[]::new);
        Map<String, Object> map = new HashMap<>(10);
        for (int i = 0; i < paramNames.length; ++i) {
            map.put(paramNames[i], params[i]);
        }
        return map;
    }

    /**
     * 处理查询操作
     */
    private Object processQuery(Method method, Map<String, Object> paramMap) {
        String sql = method.getAnnotation(Query.class).value();
        sql = PlaceholderUtils.replace(sql, paramMap);
        System.out.println("sql: " + sql);
        return executeQuery(sql, method);
    }

    /**
     * 处理更新操作
     */
    private Object processUpdate(Method method, Map<String, Object> paramMap) {
        String sql = method.getAnnotation(Update.class).value();
        sql = PlaceholderUtils.replace(sql, paramMap);
        System.out.println("sql: " + sql);
        return executeUpdate(sql, method);
    }

    /**
     * 执行查询操作并返回结果
     */
    private Object executeQuery(String sql, Method method) {
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

    /**
     * 获取动态sql字符串
     */
    private String getDynamicSql(Class<?> type, String methodName, Method daoMethod, Object[] params) {
        try {
            Constructor<?> constructor = type.getConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();

            Method method = type.getMethod(methodName, daoMethod.getParameterTypes());
            method.setAccessible(true);
            return (String) method.invoke(instance, params);
        } catch (Exception e) {
            throw new ByxOrmException("Cannot get dynamic sql of: " + daoMethod, e);
        }
    }

    /**
     * 获取动态查询的sql
     */
    private String getDynamicQuerySql(Method method, Object[] params) {
        DynamicQuery dqa = method.getAnnotation(DynamicQuery.class);
        Class<?> type = dqa.type();
        String methodName = "".equals(dqa.method()) ? method.getName() : dqa.method();
        return getDynamicSql(type, methodName, method, params);
    }

    /**
     * 获取动态更新的sql
     */
    private String getDynamicUpdateSql(Method method, Object[] params) {
        DynamicUpdate dua = method.getAnnotation(DynamicUpdate.class);
        Class<?> type = dua.type();
        String methodName = "".equals(dua.method()) ? method.getName() : dua.method();
        return getDynamicSql(type, methodName, method, params);
    }

    /**
     * 处理动态查询并返回结果
     */
    private Object processDynamicQuery(Method method, Object[] params) {
        String sql = getDynamicQuerySql(method, params);
        sql = PlaceholderUtils.replace(sql, getParamMap(method, params));
        System.out.println("sql: " + sql);
        return executeQuery(sql, method);
    }

    /**
     * 处理动态更新并返回结果
     */
    private Object processDynamicUpdate(Method method, Object[] params) {
        String sql = getDynamicUpdateSql(method, params);
        sql = PlaceholderUtils.replace(sql, getParamMap(method, params));
        System.out.println("sql: " + sql);
        return executeUpdate(sql, method);
    }
}
