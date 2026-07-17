package cn.zhangdx.fileupload.processor;

import cn.zhangdx.fileupload.request.FileUploadRequest;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author zhangdx
 * @date 2026/5/14 14:27
 */
public class FileUploadProcessorChain {

    private volatile List<FileUploadProcessor> processors = List.of();

    public void doProcess(FileUploadRequest fileUploadRequest) {
        Objects.requireNonNull(fileUploadRequest, "fileUploadRequest must not be null");
        for (FileUploadProcessor processor : processors) {
            processor.process(fileUploadRequest);
        }
    }

    public synchronized void addProcessor(FileUploadProcessor processor) {
        List<FileUploadProcessor> newProcessors = new ArrayList<>(processors);
        newProcessors.add(Objects.requireNonNull(processor, "processor must not be null"));
        newProcessors.sort(AnnotationAwareOrderComparator.INSTANCE);
        processors = List.copyOf(newProcessors);
    }

    public synchronized void batchAddProcessor(List<FileUploadProcessor> processors) {
        Objects.requireNonNull(processors, "processors must not be null");
        List<FileUploadProcessor> newProcessors = new ArrayList<>(this.processors);
        processors.forEach(processor -> newProcessors.add(
                Objects.requireNonNull(processor, "processor must not be null")));
        newProcessors.sort(AnnotationAwareOrderComparator.INSTANCE);
        this.processors = List.copyOf(newProcessors);
    }
}
