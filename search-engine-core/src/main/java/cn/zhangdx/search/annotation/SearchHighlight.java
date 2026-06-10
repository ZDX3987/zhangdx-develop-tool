package cn.zhangdx.search.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记搜索支持高亮的字段
 * @author zhangdx
 * @date 2026/5/9 13:17
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchHighlight {

    String value() default "";
}
