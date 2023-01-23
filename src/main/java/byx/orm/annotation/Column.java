package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定JavaBean字段对应的数据库列名
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Column {
    /**
     * 该字段对应数据库表的列名
     */
    String value();
}
