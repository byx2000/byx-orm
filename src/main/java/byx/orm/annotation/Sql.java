package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定Sql语句
 *
 * @author byx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Sql {
    String value();
}
