package cn.zhangdx.fileupload.processor;

import cn.zhangdx.fileupload.request.FileUploadRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

/**
 * 文件名处理器类
 * @author zhangdx
 * @date 2026/6/5 21:59
 */
@Slf4j
public class GeneralFileNameProcessor extends AbstractFileUploadProcessor {

    /**
     * 处理逻辑实现
     *
     * @param fileUploadRequest 上传请求参数
     */
    @Override
    public void process(FileUploadRequest fileUploadRequest) {
        String fileName = fileUploadRequest.getFileName();
        if (fileName == null) {
            fileName = fileUploadRequest.getFile().getOriginalFilename();
        }
        String suffix = subLowerCaseExtensionName(fileName);
        String generalFileName = UUID.randomUUID() + suffix;
        fileUploadRequest.setFileName(generalFileName);
        log.info("process rename: {}", generalFileName);
        super.nextProcess(fileUploadRequest);
    }

    /**
     * 获取文件名中的扩展名
     * @param originalFilename 原始文件名
     * @return 扩展名
     */
    private String subLowerCaseExtensionName(String originalFilename) {
        return Optional.ofNullable(originalFilename).map(name -> name.substring(name.lastIndexOf(".")))
                .orElse("").toLowerCase();
    }
}
