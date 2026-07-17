package cn.zhangdx.fileupload.server;

import cn.zhangdx.fileupload.exception.FileUploadException;
import cn.zhangdx.fileupload.processor.FileUploadProcessorChain;
import cn.zhangdx.fileupload.request.FileUploadRequest;
import cn.zhangdx.fileupload.result.FileUploadResult;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AbstractFileUploadServerTest extends TestCase {

    public void testUploadReturnsStructuredResultAndClosesInput() {
        TrackingInputStream input = new TrackingInputStream(new byte[] {1, 2, 3});
        FileUploadRequest request = FileUploadRequest.build(input, "test.txt", List.of("user", "document"));
        TestFileUploadServer server = new TestFileUploadServer();

        FileUploadResult result = server.uploadFileWithResult(request);

        assertEquals("user/document/test.txt", result.fileKey());
        assertEquals("https://files.example/user/document/test.txt", result.accessibleUrl());
        assertTrue(input.closed);
    }

    public void testRejectInvalidPathSegment() {
        FileUploadRequest request = FileUploadRequest.build(
                new ByteArrayInputStream(new byte[] {1}), "test.txt", List.of(".."));
        try {
            new TestFileUploadServer().uploadFile(request);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertTrue(e.getMessage().contains("非法的文件路径片段"));
        }
    }

    private static final class TestFileUploadServer extends AbstractFileUploadServer {
        private TestFileUploadServer() {
            super(new FileUploadProcessorChain());
        }

        @Override
        protected String doUploadFile(InputStream inputStream, String fileKey) throws FileUploadException {
            try {
                inputStream.readAllBytes();
                return getAccessibleDomain() + "/" + fileKey;
            } catch (IOException e) {
                throw FileUploadException.uploadFail(e.getMessage());
            }
        }

        @Override
        public String getAccessibleDomain() {
            return "https://files.example";
        }
    }

    private static final class TrackingInputStream extends ByteArrayInputStream {
        private boolean closed;

        private TrackingInputStream(byte[] bytes) {
            super(bytes);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }
}
