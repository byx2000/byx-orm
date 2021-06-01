package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定更新SQL
 *
 * @author byx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Update {
    String value();
}
