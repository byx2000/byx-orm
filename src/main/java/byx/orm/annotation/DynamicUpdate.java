package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定生成动态更新SQL字符串的类和方法
 *
 * @author byx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DynamicUpdate {
    Class<?> type();
    String method() default "";
}
