package cn.zhangdx.support.exception;

import lombok.Data;

/**
 * 自定义抽象Rest接口响应异常类
 * @author zhangdx
 * @date 2026/7/2 22:44
 */
@Data
public abstract class AbstractRestException extends RuntimeException {

    private final Integer code;

    protected AbstractRestException(Integer code) {
        this.code = code;
    }

    protected AbstractRestException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public String getMsg() {
        return this.getMessage();
    }
}
