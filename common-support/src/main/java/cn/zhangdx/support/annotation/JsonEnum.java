package cn.zhangdx.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author zhangdx
 * @date 2026/10/3 13:54
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonEnum {

    /**
     * 标记枚举的标识字段
     * @return 标识字段
     */
    String identity() default "";

    /**
     * JSON转化方式，默认是按值转换
     * @return JSON转化方式
     */
    Shape shape() default Shape.VALUE;

    enum Shape {

        /**
         * 按值转换
         */
        VALUE,

        /**
         * 转换对象
         */
        OBJECT
    }
}
