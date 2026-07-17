package cn.zhangdx.fileupload.processor;

import cn.zhangdx.fileupload.request.FileUploadRequest;
import junit.framework.TestCase;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class ImageFileUploadProcessorTest extends TestCase {

    public void testConvertUpperCaseExtensionThroughTemporaryFile() throws Exception {
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB), "png", png);
        FileUploadRequest request = FileUploadRequest.build(
                new ByteArrayInputStream(png.toByteArray()), "photo.PNG", List.of());

        new ImageFileUploadProcessor(0.75f, 256, 1).process(request);

        assertEquals("photo.webp", request.getFileName());
        assertTrue(request.openFileInputStream().readAllBytes().length > 0);
        request.close();
    }
}
