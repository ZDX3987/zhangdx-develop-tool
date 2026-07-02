package cn.zhangdx.support.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Rest接口自定义响应码
 * @author zhangdx
 * @date 2026/7/2 22:41
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(200, "success"),
    FAILURE(500, "failure"),
    UNAUTHORIZED(401, "您还没有登录"),
    FORBIDDEN(403, "没有相应权限"),
    NOT_FOUND(404, "页面不存在"),
    FILE_UPLOAD_FAIL(5001, "文件上传失败"),

    OAUTH_REPEAT(4001, "该账号已经关联本系统的其他账号，请先解绑")
    ;

    private final Integer code;
    private final String msg;
}
