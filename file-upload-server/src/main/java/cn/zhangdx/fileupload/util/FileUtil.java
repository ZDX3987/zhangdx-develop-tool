package cn.zhangdx.fileupload.util;

import cn.zhangdx.fileupload.exception.FileUploadException;
import com.luciad.imageio.webp.WebPWriteParam;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

/**
 * 文件工具类
 * @author zhangdx
 * @date 2026/7/17 21:35
 */
@Slf4j
@UtilityClass
public class FileUtil {

    public static final float DEFAULT_WEBP_COMPRESSION_QUALITY = 0.75f;

    public static final long DEFAULT_MAX_IMAGE_PIXELS = 20_000_000L;

    /**
     * 将图片转换到内存字节数组。大图片场景优先使用 OutputStream 重载，避免额外占用结果字节数组内存。
     */
    public static byte[] convertToWebImage(MultipartFile sourceFile) throws FileUploadException {
        Objects.requireNonNull(sourceFile, "sourceFile must not be null");
        try (InputStream sourceFileIs = sourceFile.getInputStream()) {
            return convertToWebImage(sourceFileIs, DEFAULT_WEBP_COMPRESSION_QUALITY, DEFAULT_MAX_IMAGE_PIXELS);
        } catch (IOException e) {
            log.error("convertToWebImage error: ", e);
            throw FileUploadException.uploadFail("图片转换为Webp类型失败");
        }
    }

    /**
     * 获取文件的可访问URL，包含了域名和文件全路径。
     *
     * @param fileName 文件名
     * @param path     多级文件夹名
     * @return 件的可访问地址，例如: https://file.zhangdx.cn/folder/IMG_20991201123018.jpg
     */
    public static String getAccessibleUrl(String fileName, String... path) {
        if (!StringUtils.hasText(fileName)) {
            throw FileUploadException.uploadFail("文件名不合法");
        }
        return fileName.startsWith("http") ? fileName : String.join("/", path) + "/" + fileName;
    }

    /**
     * 将输入图片的第一帧转换为 WebP。输入流的关闭由调用方负责。
     *
     * @param source 图片输入流
     * @param compressionQuality WebP 压缩质量，范围为 0 到 1
     * @param maxPixels 允许解码的最大像素数
     * @return WebP 图片字节
     */
    public static byte[] convertToWebImage(InputStream source, float compressionQuality, long maxPixels) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            convertToWebImage(source, output, compressionQuality, maxPixels);
            return output.toByteArray();
        } catch (IOException e) {
            log.error("convertToWebImage error: ", e);
            throw FileUploadException.uploadFail("图片转换为WebP类型失败");
        }
    }

    /**
     * 将输入图片的第一帧转换到 WebP 输出流。该方法不会关闭调用方传入的输入流和输出流。
     */
    public static void convertToWebImage(InputStream source, OutputStream target, float compressionQuality,
                                         long maxPixels) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(target, "target must not be null");
        if (compressionQuality < 0.0f || compressionQuality > 1.0f) {
            throw new IllegalArgumentException("compressionQuality must be between 0 and 1");
        }
        if (maxPixels <= 0) {
            throw new IllegalArgumentException("maxPixels must be greater than 0");
        }

        try {
            BufferedImage bufferedImage = readImage(source, maxPixels);
            try {
                writeWebImage(bufferedImage, target, compressionQuality);
            } finally {
                bufferedImage.flush();
            }
        } catch (FileUploadException e) {
            throw e;
        } catch (Exception | LinkageError e) {
            log.error("convertToWebImage error: ", e);
            throw FileUploadException.uploadFail("图片转换为WebP类型失败");
        }
    }

    /**
     * 打开一个关闭时自动删除源文件的输入流。
     */
    public static InputStream openDeleteOnCloseInputStream(Path file) throws IOException {
        Objects.requireNonNull(file, "file must not be null");
        InputStream inputStream = Files.newInputStream(file);
        return new FilterInputStream(inputStream) {
            private boolean closed;

            @Override
            public void close() throws IOException {
                if (closed) {
                    return;
                }
                closed = true;
                IOException closeException = null;
                try {
                    super.close();
                } catch (IOException e) {
                    closeException = e;
                }
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    if (closeException != null) {
                        closeException.addSuppressed(e);
                    } else {
                        closeException = e;
                    }
                }
                if (closeException != null) {
                    throw closeException;
                }
            }
        };
    }

    /**
     * 获取文件名中的扩展名
     * @param originalFilename 原始文件名
     * @return 扩展名
     */
    public static String subLowerCaseExtensionName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "";
        }
        int separatorIndex = Math.max(originalFilename.lastIndexOf('/'), originalFilename.lastIndexOf('\\'));
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex <= separatorIndex || dotIndex == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * 只替换最后一个扩展名，原文件名没有扩展名时直接追加。
     */
    public static String replaceExtension(String fileName, String extension) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName must not be blank");
        }
        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("extension must not be blank");
        }
        String normalizedExtension = extension.startsWith(".") ? extension.substring(1) : extension;
        int separatorIndex = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        int dotIndex = fileName.lastIndexOf('.');
        int extensionStart = dotIndex > separatorIndex ? dotIndex : fileName.length();
        return fileName.substring(0, extensionStart) + "." + normalizedExtension.toLowerCase(Locale.ROOT);
    }

    private static BufferedImage readImage(InputStream source, long maxPixels) throws IOException {
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(source)) {
            if (imageInputStream == null) {
                throw FileUploadException.uploadFail("无法创建图片输入流");
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
            if (!readers.hasNext()) {
                throw FileUploadException.uploadFail("文件内容不是支持的图片格式");
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(imageInputStream, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                long pixels = (long) width * height;
                if (pixels > maxPixels) {
                    throw FileUploadException.uploadFail(
                            "图片像素超过限制: " + width + "x" + height + ", 最大允许 " + maxPixels);
                }
                BufferedImage bufferedImage = reader.read(0);
                if (bufferedImage == null) {
                    throw FileUploadException.uploadFail("无法解码图片内容");
                }
                return bufferedImage;
            } finally {
                reader.dispose();
            }
        }
    }

    private static void writeWebImage(BufferedImage bufferedImage, OutputStream target, float compressionQuality)
            throws IOException {
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType("image/webp");
        if (!imageWriters.hasNext()) {
            throw FileUploadException.uploadFail("未找到 WebP 图片编码器");
        }
        ImageWriter writer = imageWriters.next();
        OutputStream nonClosingTarget = new FilterOutputStream(target) {
            @Override
            public void close() throws IOException {
                flush();
            }
        };
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(nonClosingTarget)) {
            if (ios == null) {
                throw FileUploadException.uploadFail("无法创建 WebP 图片输出流");
            }
            WebPWriteParam webPWriteParam = new WebPWriteParam(writer.getLocale());
            webPWriteParam.setCompressionMode(WebPWriteParam.MODE_EXPLICIT);
            webPWriteParam.setCompressionType(webPWriteParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            webPWriteParam.setCompressionQuality(compressionQuality);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(bufferedImage, null, null), webPWriteParam);
            ios.flush();
        } finally {
            writer.dispose();
        }
    }
}
