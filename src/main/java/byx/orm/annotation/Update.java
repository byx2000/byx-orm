package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定更新sql
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Update {
    /**
     * sql
     */
    String value();
}
