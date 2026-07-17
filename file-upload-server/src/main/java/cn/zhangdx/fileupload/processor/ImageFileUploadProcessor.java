package cn.zhangdx.fileupload.processor;

import cn.zhangdx.fileupload.exception.FileUploadException;
import cn.zhangdx.fileupload.request.FileUploadRequest;
import cn.zhangdx.fileupload.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * 图片类型转换类
 * @author zhangdx
 * @date 2026/5/14 13:29
 */
@Slf4j
public class ImageFileUploadProcessor extends AbstractFileUploadProcessor {

    private static final Set<String> SUPPORT_IMAGE_FILE_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp");

    private static final String WEBP_EXTENSION = "webp";

    private final float compressionQuality;

    private final long maxPixels;

    private final Semaphore conversionPermits;

    public ImageFileUploadProcessor() {
        this(FileUtil.DEFAULT_WEBP_COMPRESSION_QUALITY, FileUtil.DEFAULT_MAX_IMAGE_PIXELS, 2);
    }

    public ImageFileUploadProcessor(float compressionQuality, long maxPixels) {
        this(compressionQuality, maxPixels, 2);
    }

    public ImageFileUploadProcessor(float compressionQuality, long maxPixels, int maxConcurrentConversions) {
        if (compressionQuality < 0.0f || compressionQuality > 1.0f) {
            throw new IllegalArgumentException("compressionQuality must be between 0 and 1");
        }
        if (maxPixels <= 0) {
            throw new IllegalArgumentException("maxPixels must be greater than 0");
        }
        if (maxConcurrentConversions <= 0) {
            throw new IllegalArgumentException("maxConcurrentConversions must be greater than 0");
        }
        this.compressionQuality = compressionQuality;
        this.maxPixels = maxPixels;
        this.conversionPermits = new Semaphore(maxConcurrentConversions, true);
    }

    /**
     * 处理逻辑实现
     *
     * @param fileUploadRequest 上传请求参数
     */
    @Override
    public void process(FileUploadRequest fileUploadRequest) {
        String fileName = fileUploadRequest.getFileName();
        String suffix = FileUtil.subLowerCaseExtensionName(fileName);
        if (!WEBP_EXTENSION.equals(suffix) && SUPPORT_IMAGE_FILE_EXTENSIONS.contains(suffix)) {
            acquireConversionPermit();
            try {
                convertToWebp(fileUploadRequest, fileName);
            } finally {
                conversionPermits.release();
            }
        }
    }

    private void convertToWebp(FileUploadRequest fileUploadRequest, String fileName) {
        String webpFileName = FileUtil.replaceExtension(fileName, WEBP_EXTENSION);
        Path convertedFile = null;
        boolean temporaryFileHandedOff = false;
        try (InputStream source = fileUploadRequest.openFileInputStream()) {
            convertedFile = Files.createTempFile("file-upload-", ".webp");
            try (OutputStream target = Files.newOutputStream(convertedFile)) {
                FileUtil.convertToWebImage(source, target, compressionQuality, maxPixels);
            }
            fileUploadRequest.setFileName(webpFileName);
            fileUploadRequest.setFileInputStream(FileUtil.openDeleteOnCloseInputStream(convertedFile));
            temporaryFileHandedOff = true;
            log.info("convert image to webp file, fileName: {}", webpFileName);
        } catch (IOException e) {
            throw FileUploadException.uploadFail("图片转换临时文件处理失败");
        } finally {
            if (convertedFile != null && !temporaryFileHandedOff) {
                try {
                    Files.deleteIfExists(convertedFile);
                } catch (IOException e) {
                    log.warn("delete converted temporary image failed: {}", convertedFile, e);
                }
            }
        }
    }

    private void acquireConversionPermit() {
        try {
            conversionPermits.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw FileUploadException.uploadFail("等待图片转换资源时被中断");
        }
    }
}
