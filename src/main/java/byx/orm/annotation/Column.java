package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定实体类字段对应的数据库列名
 *
 * @author byx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Column {
    String value();
}
