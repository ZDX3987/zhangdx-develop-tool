package cn.zhangdx.fileupload.processor;

import cn.zhangdx.fileupload.request.FileUploadRequest;
import cn.zhangdx.fileupload.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * 图片类型转换类
 * @author zhangdx
 * @date 2026/5/14 13:29
 */
@Slf4j
public class ImageFileUploadProcessor extends AbstractFileUploadProcessor {

    private static final Set<String> SUPPORT_IMAGE_FILE_EXTENSIONS = new HashSet<>() {{
        add("jpg");
        add("jpeg");
        add("png");
        add("gif");
        add("bmp");
        add("tiff");
        add("webp");
    }};

    private static final String IMAGE_FILE_CONVERT_EXTENSIONS = "webp";

    /**
     * 处理逻辑实现
     *
     * @param fileUploadRequest 上传请求参数
     */
    @Override
    public void process(FileUploadRequest fileUploadRequest) {
        MultipartFile file = fileUploadRequest.getFile();
        String fileName = fileUploadRequest.getFileName();
        String suffix = FileUtil.subLowerCaseExtensionName(fileName);
        if (!IMAGE_FILE_CONVERT_EXTENSIONS.equals(suffix) && SUPPORT_IMAGE_FILE_EXTENSIONS.contains(suffix)) {
            fileUploadRequest.setFileName(FileUtil.generateImgFileName());
            log.info("convert image to webp file, fileName: {}", fileUploadRequest.getFileName());
            byte[] bytes = FileUtil.convertToWebImage(file);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            fileUploadRequest.setFileInputStream(inputStream);
        }
        super.nextProcess(fileUploadRequest);
    }
}