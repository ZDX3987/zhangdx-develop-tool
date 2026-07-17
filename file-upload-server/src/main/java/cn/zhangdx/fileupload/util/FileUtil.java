package cn.zhangdx.fileupload.util;

import cn.zhangdx.fileupload.exception.FileUploadException;
import com.luciad.imageio.webp.WebPWriteParam;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * 文件工具类
 * @author zhangdx
 * @date 2026/7/17 21:35
 */
@Slf4j
@UtilityClass
public class FileUtil {

    private static final String DEFAULT_FILE_NAME_PREFIX = "ZHANGDX";
    private static final String DEFAULT_FILE_NAME_SEPARATOR = "_";

    public static byte[] convertToWebImage(MultipartFile sourceFile) throws FileUploadException {
        try {
            InputStream sourceFileIs = sourceFile.getInputStream();
            BufferedImage bufferedImage = ImageIO.read(sourceFileIs);
            return doConvertBufferedImage(bufferedImage);
        } catch (Exception e) {
            log.error("convertToWebImage error: ", e);
            throw FileUploadException.uploadFail("图片转换为Webp类型失败");
        }
    }

    private static byte[] doConvertBufferedImage(BufferedImage bufferedImage) throws IOException {
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType("image/webp");
        ImageWriter writer = imageWriters.next();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(bos)) {
            WebPWriteParam webPWriteParam = new WebPWriteParam(writer.getLocale());
            webPWriteParam.setCompressionMode(WebPWriteParam.MODE_EXPLICIT);
            webPWriteParam.setCompressionType(webPWriteParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            webPWriteParam.setCompressionQuality(0.75f);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(bufferedImage, null, null), webPWriteParam);
            ios.flush();
            bos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("doConvertBufferedImage error: ", e);
            throw e;
        } finally {
            writer.dispose();
        }
    }
}
