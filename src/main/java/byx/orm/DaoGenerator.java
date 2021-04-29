package byx.orm;

import byx.orm.annotation.Query;
import byx.orm.annotation.Update;
import byx.orm.util.MapUtils;
import byx.orm.util.PlaceholderUtils;
import byx.util.jdbc.JdbcUtils;
import byx.util.jdbc.core.MapRowMapper;
import byx.util.proxy.ProxyUtils;
import byx.util.proxy.core.MethodInterceptor;
import byx.util.proxy.core.MethodSignature;
import byx.util.proxy.core.TargetMethod;

import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // 获取带占位符的sql
        String sql = signature.getAnnotation(Query.class).value();

        // 替换占位符
        sql = PlaceholderUtils.replace(sql, paramMap);
        System.out.println("sql: " + sql);

        // 获取方法返回值类型
        Class<?> resultType = signature.getReturnType();

        // 如果返回值是基本类型，则查询单个值
        // 如果返回值不是列表，则查询单行数据
        // 否则，查询列表
        if (resultType == int.class || resultType == Integer.class ||
                resultType == double.class || resultType == Double.class ||
                resultType == String.class) {
            return jdbcUtils.querySingleValue(sql);
        } else if (signature.getReturnType() != List.class) {
            Map<String, Object> resultMap = jdbcUtils.querySingleRow(sql, new MapRowMapper());
            // 查询结果为空
            if (resultMap == null) {
                return null;
            }
            return MapUtils.mapToObject(resultType, resultMap);
        } else {
            // 获取返回值列表的泛型参数
            Type t = signature.getGenericReturnType();
            if (t instanceof ParameterizedType) {
                resultType = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
            } else {
                throw new RuntimeException("泛型参数不正确");
            }

            List<Map<String, Object>> resultMapList = jdbcUtils.queryList(sql, new MapRowMapper());
            List<Object> resultList = new ArrayList<>();
            for (Map<String, Object> m : resultMapList) {
                resultList.add(MapUtils.mapToObject(resultType, m));
            }
            return resultList;
        }
    }

    /**
     * 处理更新操作
     */
    private Object processUpdate(MethodSignature signature, Map<String, Object> paramMap) {
        // 获取带占位符的sql
        String sql = signature.getAnnotation(Update.class).value();

        // 替换占位符
        sql = PlaceholderUtils.replace(sql, paramMap);
        System.out.println("sql: " + sql);

        // 如果方法返回值为void，则直接执行更新操作
        // 否则返回影响行数
        if (signature.getReturnType() == void.class) {
            jdbcUtils.update(sql);
            return null;
        } else {
            return jdbcUtils.update(sql);
        }
    }
}
