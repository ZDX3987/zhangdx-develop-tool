package cn.zhangdx.fileupload.processor;

import cn.zhangdx.fileupload.request.FileUploadRequest;

/**
 * 文件上传处理器（责任链模式）
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

    /**
     * 设置下一个处理器
     * @param nextProcessor 下一个处理器对象
     */
    void setNextProcessor(FileUploadProcessor nextProcessor);

}