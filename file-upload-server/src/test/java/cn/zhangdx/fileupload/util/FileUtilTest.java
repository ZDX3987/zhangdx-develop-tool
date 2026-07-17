package cn.zhangdx.fileupload.util;

import cn.zhangdx.fileupload.exception.FileUploadException;
import junit.framework.TestCase;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtilTest extends TestCase {

    public void testExtensionOperations() {
        assertEquals("jpg", FileUtil.subLowerCaseExtensionName("photo.JPG"));
        assertEquals("", FileUtil.subLowerCaseExtensionName("README"));
        assertEquals("archive.tar.webp", FileUtil.replaceExtension("archive.tar.GZ", "webp"));
        assertEquals("README.webp", FileUtil.replaceExtension("README", ".webp"));
    }

    public void testConvertToWebImageOutputStream() throws Exception {
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(image, "png", png);

        ByteArrayOutputStream webp = new ByteArrayOutputStream();
        FileUtil.convertToWebImage(new ByteArrayInputStream(png.toByteArray()), webp, 0.75f, 256);

        assertTrue(webp.size() > 0);
    }

    public void testRejectImageOverPixelLimit() throws Exception {
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(image, "png", png);

        try {
            FileUtil.convertToWebImage(new ByteArrayInputStream(png.toByteArray()),
                    new ByteArrayOutputStream(), 0.75f, 255);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertTrue(e.getMessage().contains("图片像素超过限制"));
        }
    }

    public void testDeleteOnCloseInputStream() throws Exception {
        Path file = Files.createTempFile("file-util-test-", ".tmp");
        Files.write(file, new byte[] {1});

        try (InputStream inputStream = FileUtil.openDeleteOnCloseInputStream(file)) {
            assertEquals(1, inputStream.read());
            assertTrue(Files.exists(file));
        }

        assertFalse(Files.exists(file));
    }
}
