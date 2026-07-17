package cn.zhangdx.fileupload.processor;

import cn.zhangdx.fileupload.request.FileUploadRequest;
import junit.framework.TestCase;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

public class FileUploadProcessorChainTest extends TestCase {

    public void testEmptyChainIsNoOp() {
        new FileUploadProcessorChain().doProcess(new FileUploadRequest());
    }

    public void testProcessorsAreOrderedWithoutMutatingInputList() {
        List<Integer> calls = new ArrayList<>();
        List<FileUploadProcessor> processors = List.of(new SecondProcessor(calls), new FirstProcessor(calls));
        FileUploadProcessorChain chain = new FileUploadProcessorChain();

        chain.batchAddProcessor(processors);
        chain.doProcess(new FileUploadRequest());

        assertEquals(List.of(1, 2), calls);
        assertTrue(processors.get(0) instanceof SecondProcessor);
    }

    @Order(1)
    private static final class FirstProcessor implements FileUploadProcessor {
        private final List<Integer> calls;

        private FirstProcessor(List<Integer> calls) {
            this.calls = calls;
        }

        @Override
        public void process(FileUploadRequest fileUploadRequest) {
            calls.add(1);
        }
    }

    @Order(2)
    private static final class SecondProcessor implements FileUploadProcessor {
        private final List<Integer> calls;

        private SecondProcessor(List<Integer> calls) {
            this.calls = calls;
        }

        @Override
        public void process(FileUploadRequest fileUploadRequest) {
            calls.add(2);
        }
    }
}
