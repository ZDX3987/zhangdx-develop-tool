package cn.zhangdx.fileupload.processor;

import cn.zhangdx.fileupload.request.FileUploadRequest;

/**
 * 文件上传处理器抽象类
 * @author zhangdx
 * @date 2026/5/14 14:34
 */
public abstract class AbstractFileUploadProcessor implements FileUploadProcessor {

    private FileUploadProcessor nextProcessor;

    /**
     * 设置下一个处理器
     *
     * @param nextProcessor 下一个处理器对象
     */
    @Override
    public void setNextProcessor(FileUploadProcessor nextProcessor) {
        if (this.nextProcessor == null) {
            this.nextProcessor = nextProcessor;
        } else {
            this.nextProcessor.setNextProcessor(nextProcessor);
        }
    }

    /**
     * 处理逻辑实现
     *
     * @param fileUploadRequest 上传请求参数
     */
    @Override
    public void process(FileUploadRequest fileUploadRequest) {
        nextProcess(fileUploadRequest);
    }

    protected void nextProcess(FileUploadRequest fileUploadRequest) {
        if (nextProcessor != null) {
            nextProcessor.process(fileUploadRequest);
        }
    }
}