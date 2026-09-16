package cn.zhangdx.distributed.idempotent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义支持幂等方法的注解
 * @author zhangdx
 * @date 2026/8/17 21:27
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 指定所属命名空间
     * @return
     */
    String namespace() default "";

    /**
     * 指定幂等key，支持SpEL动态获取方法参数
     * @return
     */
    String key() default "";

    /**
     * 处理中标志结束时间
     * @return
     */
    int processingTtl() default 0;

    /**
     * 成功标志结束时间
     * @return
     */
    int successTtl() default 0;

    /**
     * 是否缓存首次请求响应
     * @return
     */
    boolean cacheResult() default true;

    /**
     * 是否检查相同key下请求参数一致性
     * @return
     */
    boolean checkRequestHash() default true;
}
