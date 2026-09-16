package cn.zhangdx.distributed.idempotent.enumeration;

/**
 * 幂等处理中间状态
 * @author zhangdx
 * @date 2026/8/17 21:42
 */
public enum IdempotencyStatus {

    /**
     * 处理中
     */
    PROCESSING,

    /**
     * 处理成功
     */
    SUCCESS

}
