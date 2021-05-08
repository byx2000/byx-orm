package byx.orm;

import byx.orm.annotation.DynamicQuery;
import byx.orm.annotation.DynamicUpdate;
import byx.orm.annotation.Query;
import byx.orm.annotation.Update;
import byx.orm.core.ObjectMapper;
import byx.orm.core.PlaceholderProcessor;
import byx.util.jdbc.JdbcUtils;
import byx.util.jdbc.core.MapRowMapper;
import byx.util.proxy.ProxyUtils;
import byx.util.proxy.core.MethodInterceptor;
import byx.util.proxy.core.MethodSignature;
import byx.util.proxy.core.TargetMethod;

import javax.sql.DataSource;
import java.lang.reflect.*;
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
     * 生成Dao接口的实现类
     * @param daoInterface Dao接口类型
     * @param <T> 接口类型
     * @return 接口实现类
     */
    public <T> T generate(Class<T> daoInterface) {
        return ProxyUtils.implement(daoInterface, new DaoMethodInterceptor());
    }

    /**
     * Dao的方法拦截器
     */
    private class DaoMethodInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(TargetMethod targetMethod) {
            // 获取方法签名
            MethodSignature signature = targetMethod.getSignature();

            // 计算方法参数Map
            Map<String, Object> paramMap = getParamMap(targetMethod);

            if (signature.hasAnnotation(Query.class)) {
                return processQuery(signature, paramMap);
            } else if (signature.hasAnnotation(Update.class)) {
                return processUpdate(signature, paramMap);
            } else if (signature.hasAnnotation(DynamicQuery.class)) {
                return processDynamicQuery(targetMethod);
            } else if (signature.hasAnnotation(DynamicUpdate.class)) {
                return processDynamicUpdate(targetMethod);
            } else {
                throw new RuntimeException("方法定义不正确");
            }
        }
    }

    /**
     * 获取参数映射表
     */
    private Map<String, Object> getParamMap(TargetMethod targetMethod) {
        String[] paramNames = targetMethod.getSignature().getParameterNames();
        Object[] paramValues = targetMethod.getParams();
        Map<String, Object> map = new HashMap<>(10);
        for (int i = 0; i < paramNames.length; ++i) {
            map.put(paramNames[i], paramValues[i]);
        }
        return map;
    }

    /**
     * 处理查询操作
     */
    private Object processQuery(MethodSignature signature, Map<String, Object> paramMap) {
        String sql = signature.getAnnotation(Query.class).value();
        sql = PlaceholderProcessor.replace(sql, paramMap);
        System.out.println("sql: " + sql);
        return executeQuery(sql, signature);
    }

    /**
     * 处理更新操作
     */
    private Object processUpdate(MethodSignature signature, Map<String, Object> paramMap) {
        String sql = signature.getAnnotation(Update.class).value();
        sql = PlaceholderProcessor.replace(sql, paramMap);
        System.out.println("sql: " + sql);
        return executeUpdate(sql, signature);
    }

    /**
     * 执行查询操作并返回结果
     */
    private Object executeQuery(String sql, MethodSignature signature) {
        // 执行sql，获取结果集
        List<Map<String, Object>> resultList = jdbcUtils.queryList(sql, new MapRowMapper());

        // 获取方法返回值类型
        Class<?> returnType = signature.getReturnType();

        // 如果返回值是列表，则获取列表的泛型参数类型，并把结果集的每一行转换成该类型
        // 否则，直接把结果集的第一行转换成返回值类型
        if (returnType == List.class) {
            Type t = signature.getGenericReturnType();
            if (t instanceof ParameterizedType) {
                Class<?> resultType = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
                return resultList.stream().map(m -> ObjectMapper.mapToObject(resultType, m)).collect(Collectors.toList());
            } else {
                throw new RuntimeException("泛型参数不正确");
            }
        } else {
            if (resultList.isEmpty()) {
                return null;
            }
            if (resultList.size() > 1) {
                throw new RuntimeException("结果集行数大于1");
            }
            return ObjectMapper.mapToObject(returnType, resultList.get(0));
        }
    }

    /**
     * 执行更新操作并返回结果
     */
    private Object executeUpdate(String sql, MethodSignature signature) {
        // 如果方法返回值为void，则直接执行更新操作
        // 否则返回影响行数
        if (signature.getReturnType() == void.class) {
            jdbcUtils.update(sql);
            return null;
        } else {
            return jdbcUtils.update(sql);
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
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取动态查询的sql
     */
    private String getDynamicQuerySql(TargetMethod targetMethod) {
        MethodSignature signature = targetMethod.getSignature();
        DynamicQuery dqa = signature.getAnnotation(DynamicQuery.class);
        Class<?> type = dqa.type();
        String methodName = "".equals(dqa.method()) ? signature.getName() : dqa.method();
        return getDynamicSql(type, methodName, targetMethod.getMethod(), targetMethod.getParams());
    }

    /**
     * 获取动态更新的sql
     */
    private String getDynamicUpdateSql(TargetMethod targetMethod) {
        MethodSignature signature = targetMethod.getSignature();
        DynamicUpdate dua = signature.getAnnotation(DynamicUpdate.class);
        Class<?> type = dua.type();
        String methodName = "".equals(dua.method()) ? signature.getName() : dua.method();
        return getDynamicSql(type, methodName, targetMethod.getMethod(), targetMethod.getParams());
    }

    /**
     * 处理动态查询并返回结果
     */
    private Object processDynamicQuery(TargetMethod targetMethod) {
        String sql = getDynamicQuerySql(targetMethod);
        sql = PlaceholderProcessor.replace(sql, getParamMap(targetMethod));
        System.out.println("sql: " + sql);
        return executeQuery(sql, targetMethod.getSignature());
    }

    /**
     * 处理动态更新并返回结果
     */
    private Object processDynamicUpdate(TargetMethod targetMethod) {
        String sql = getDynamicUpdateSql(targetMethod);
        sql = PlaceholderProcessor.replace(sql, getParamMap(targetMethod));
        System.out.println("sql: " + sql);
        return executeUpdate(sql, targetMethod.getSignature());
    }
}
