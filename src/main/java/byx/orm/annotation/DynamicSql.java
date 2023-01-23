package byx.orm.annotation;

import java.lang.annotation.*;

/**
 * 指定生成动态sql的类和方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface DynamicSql {
    /**
     * 动态sql生成方法所在的类，该类必须有无参构造函数
     */
    Class<?> type();

    /**
     * 动态sql生成方法名，默认为dao对应方法名
     */
    String method() default "";
}
