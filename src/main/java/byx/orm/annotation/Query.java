package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定查询SQL
 *
 * @author byx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Query {
    String value();
}
