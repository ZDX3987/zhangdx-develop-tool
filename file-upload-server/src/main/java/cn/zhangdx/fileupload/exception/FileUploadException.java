package cn.zhangdx.fileupload.exception;

import cn.zhangdx.support.exception.AbstractRestException;
import cn.zhangdx.support.rest.ResponseCode;

import java.io.Serializable;

/**
 * 文件上传服务器异常
 * @author zhangdx
 * @date 2026/7/17 21:13
 */
public class FileUploadException extends AbstractRestException implements Serializable {

    private static final long serialVersionUID = -2774400575754059965L;

    protected FileUploadException(Integer code) {
        super(code);
    }

    protected FileUploadException(Integer code, String msg) {
        super(code, msg);
    }

    /**
     * 文件上传失败异常
     * @param msg 错误信息，可以为null，但是如果为空字符串，则异常的msg也为空
     * @return 包装异常信息的异常对象
     */
    public static FileUploadException uploadFail(String msg) {
        return msg != null ? new FileUploadException(ResponseCode.FILE_UPLOAD_FAIL.getCode(), msg)
                : new FileUploadException(ResponseCode.FILE_UPLOAD_FAIL.getCode());
    }
}
