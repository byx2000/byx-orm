package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定生成动态查询sql的类和方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DynamicQuery {
    /**
     * 动态sql生成方法所在的类
     */
    Class<?> type();

    /**
     * 动态sql生成方法名
     */
    String method() default "";
}
