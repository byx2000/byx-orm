package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 分隔符
 *
 * @author byx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Delimiter {
    String value();
}
