package byx.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态查询
 *
 * @author byx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DynamicQuery {
    /**
     * 提供动态sql字符串的类
     */
    Class<?> type();

    /**
     * 提供动态sql字符串的方法
     */
    String method() default "";
}