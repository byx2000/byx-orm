package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定Dao方法对应的sql
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Sql {
    /**
     * sql
     */
    String value();
}
