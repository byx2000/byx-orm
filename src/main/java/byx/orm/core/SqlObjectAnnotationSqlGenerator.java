package byx.orm.core;

import byx.orm.annotation.SqlObject;
import byx.orm.util.ObjectToSql;

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
    public boolean support(MethodContext ctx) {
        return ctx.getMethod().isAnnotationPresent(SqlObject.class);
    }

    @Override
    public String getSql(MethodContext ctx) {
        return sql = ObjectToSql.generate(ctx.getArgs()[0]);
    }

    @Override
    public SqlType getType(MethodContext ctx) {
        return sql.trim().toUpperCase(Locale.ROOT).startsWith(QUERY_PREFIX)
                ? SqlType.QUERY
                : SqlType.UPDATE;
    }
}
