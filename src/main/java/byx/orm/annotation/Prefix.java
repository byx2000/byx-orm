package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 前缀
 *
 * @author byx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Prefix {
    String value();
}
