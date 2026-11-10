package cn.zhangdx.support.exception;


/**
 * 公共ID分配失败，包括号码池未初始化、号码耗尽和数据库访问失败。
 * @author zhangdx
 * @date 2026/10/29 22:18
 */
public class IdGenerationException extends RuntimeException {
    public IdGenerationException(String message) {
        super(message);
    }

    public IdGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
