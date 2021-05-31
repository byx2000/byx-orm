package byx.orm.core;

import byx.orm.annotation.SqlObject;
import byx.orm.util.ObjectToSql;

import java.lang.reflect.Method;

/**
 * 从SqlObject生成sql字符串
 *
 * @author byx
 */
public class SqlObjectAnnotationSqlGenerator implements SqlGenerator {
    @Override
    public boolean support(Method method, Object[] params) {
        return method.isAnnotationPresent(SqlObject.class);
    }

    @Override
    public String getSql(Method method, Object[] params) {
        return ObjectToSql.generate(params[0]);
    }
}
