package cn.zhangdx.distributed.idempotent.aspect;

import cn.zhangdx.distributed.idempotent.annotation.Idempotent;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * 幂等处理拦截切面
 * @author zhangdx
 * @date 2026/8/17 21:34
 */
public class IdempotencyAspect {

    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        Method method = resolveMethod(joinPoint);

    }

    private Method resolveMethod(ProceedingJoinPoint joinPoint) {
        return null;
    }
}
