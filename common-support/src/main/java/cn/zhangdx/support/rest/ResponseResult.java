package cn.zhangdx.support.rest;

import lombok.Data;

import java.io.Serializable;

/**
 * Rest接口响应模版类
 * @author zhangdx
 * @date 2026/7/2 22:39
 */
@Data
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = -6902508908533724149L;

    private Integer code;

    private String msg;

    private T data;

    private ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), null);
    }

    public static <T> ResponseResult<T> success(String msg) {
        return new ResponseResult<>(ResponseCode.SUCCESS.getCode(), msg, null);
    }

    public static <T> ResponseResult<T> success(String msg, T data) {
        return new ResponseResult<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), data);
    }

    public static ResponseResult<Void> fail(String msg) {
        return new ResponseResult<>(ResponseCode.FAILURE.getCode(), msg, null);
    }

    public static ResponseResult<Void> fail(String msg, Integer code) {
        return new ResponseResult<>(code, msg, null);
    }

    public static <T> ResponseResult<T> unauthorized(String msg) {
        return new ResponseResult<>(ResponseCode.UNAUTHORIZED.getCode(), msg, null);
    }

    public static <T> ResponseResult<T> forbidden(String msg) {
        return new ResponseResult<>(ResponseCode.FORBIDDEN.getCode(), msg, null);
    }
}
