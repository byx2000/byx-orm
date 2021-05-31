package byx.orm.core;

import byx.orm.annotation.SqlObject;
import byx.orm.util.ObjectToSql;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * 从SqlObject生成sql字符串
 *
 * @author byx
 */
public class SqlObjectAnnotationSqlGenerator implements SqlGenerator {
    private static final String QUERY_PREFIX = "SELECT";
    private String sql;

    @Override
    public boolean support(Method method, Object[] params) {
        return method.isAnnotationPresent(SqlObject.class);
    }

    @Override
    public String getSql(Method method, Object[] params) {
        return sql = ObjectToSql.generate(params[0]);
    }

    @Override
    public SqlType getType() {
        return sql.trim().toUpperCase(Locale.ROOT).startsWith(QUERY_PREFIX)
                ? SqlType.QUERY
                : SqlType.UPDATE;
    }
}
