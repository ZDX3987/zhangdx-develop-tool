package cn.zhangdx.fileupload.processor;

import cn.zhangdx.fileupload.request.FileUploadRequest;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.List;

/**
 *
 * @author zhangdx
 * @date 2026/5/14 14:27
 */
public class FileUploadProcessorChain {

    private FileUploadProcessor firstProcessor;


    public void doProcess(FileUploadRequest fileUploadRequest) {
        if (firstProcessor != null) {
            firstProcessor.process(fileUploadRequest);
        }
    }

    public void addProcessor(FileUploadProcessor processor) {
        if (firstProcessor == null) {
            firstProcessor =  processor;
        } else {
            firstProcessor.setNextProcessor(processor);
        }
    }

    public void batchAddProcessor(List<FileUploadProcessor> processors) {
        processors.sort(AnnotationAwareOrderComparator.INSTANCE);
        for (FileUploadProcessor processor : processors) {
            addProcessor(processor);
        }
    }
}

