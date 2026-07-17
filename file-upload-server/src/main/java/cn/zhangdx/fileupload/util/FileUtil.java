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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;

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
        try (InputStream sourceFileIs = sourceFile.getInputStream()) {
            BufferedImage bufferedImage = ImageIO.read(sourceFileIs);
            return doConvertBufferedImage(bufferedImage);
        } catch (Exception e) {
            log.error("convertToWebImage error: ", e);
            throw FileUploadException.uploadFail("图片转换为Webp类型失败");
        }
    }

    /**
     * 生成图片唯一文件名（IMG_ZHANGDX_20250610072137.webp）
     * @return 图片名称
     */
    public static String generateImgFileName() {
        return genUploadFileName("IMG", ".webp");
    }

    /**
     * 获取文件名中的扩展名
     * @param originalFilename 原始文件名
     * @return 扩展名
     */
    public static String subLowerCaseExtensionName(String originalFilename) {
        return Optional.ofNullable(originalFilename).map(name -> name.substring(name.lastIndexOf(".") + 1))
                .orElse("").toLowerCase();
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

    /**
     * 生成上传文件的唯一文件名
     * @param prefix 文件名前缀
     * @param extension 扩展名
     * @return 生成唯一文件名
     */
    private String genUploadFileName(String prefix, String extension) {
        return String.join("", prefix, DEFAULT_FILE_NAME_SEPARATOR, DEFAULT_FILE_NAME_PREFIX, DEFAULT_FILE_NAME_SEPARATOR,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), extension);
    }
}
