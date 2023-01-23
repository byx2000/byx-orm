package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定查询sql
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Query {
    /**
     * sql
     */
    String value();
}
