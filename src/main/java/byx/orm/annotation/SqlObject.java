package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定从对象生成sql语句
 *
 * @author byx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface SqlObject {
}
