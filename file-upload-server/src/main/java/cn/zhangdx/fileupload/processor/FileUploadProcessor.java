package cn.zhangdx.fileupload.processor;

import cn.zhangdx.fileupload.request.FileUploadRequest;

/**
 * 文件上传处理器
 * @author zhangdx
 * @date 2026/5/14 13:24
 */
public interface FileUploadProcessor {

    /**
     * 处理逻辑实现
     *
     * @param fileUploadRequest 上传请求参数
     */
    void process(FileUploadRequest fileUploadRequest);
}
