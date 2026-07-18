package cn.zhangdx.fileupload.server;

import cn.zhangdx.fileupload.exception.FileUploadException;
import cn.zhangdx.fileupload.processor.FileUploadProcessorChain;
import cn.zhangdx.fileupload.request.FileUploadRequest;
import cn.zhangdx.fileupload.result.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传服务抽象类
 * @author zhangdx
 * @date 2026/5/14 12:52
 */
public abstract class AbstractFileUploadServer implements FileUploadServer {

    private final FileUploadProcessorChain fileUploadProcessorChain;

    protected AbstractFileUploadServer(FileUploadProcessorChain fileUploadProcessorChain) {
        this.fileUploadProcessorChain = fileUploadProcessorChain;
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 返回文件的路径
     * @throws FileUploadException 文件上传异常
     */
    @Override
    public String uploadFile(MultipartFile file) throws FileUploadException {
        return uploadFile(FileUploadRequest.build(file, List.of()));
    }

    /**
     * 文件上传
     *
     * @param fileUploadRequest 文件上传请求参数
     * @return 返回文件的路径
     * @throws FileUploadException 文件上传异常
     */
    @Override
    public String uploadFile(FileUploadRequest fileUploadRequest) throws FileUploadException {
        return uploadFileWithResult(fileUploadRequest).accessibleUrl();
    }

    @Override
    public FileUploadResult uploadFileWithResult(FileUploadRequest fileUploadRequest) throws FileUploadException {
        if (fileUploadRequest == null) {
            throw FileUploadException.uploadFail("文件上传请求不能为空");
        }
        try (fileUploadRequest) {
            validateRequest(fileUploadRequest);
            if (fileUploadProcessorChain != null) {
                fileUploadProcessorChain.doProcess(fileUploadRequest);
            }
            validateFileName(fileUploadRequest.getFileName());
            List<String> filePathList = new ArrayList<>(fileUploadRequest.getFilePath() == null
                    ? List.of() : fileUploadRequest.getFilePath());
            filePathList.forEach(this::validatePathSegment);
            filePathList.add(fileUploadRequest.getFileName());
            String fullFilePath = String.join("/", filePathList);
            InputStream inputStream = fileUploadRequest.openFileInputStream();
            String fileKey = doUploadFile(inputStream, fullFilePath);
            String accessibleUrl = getAccessibleDomain().replaceAll("/+$", "") + "/" + fileKey;
            return new FileUploadResult(fileKey, accessibleUrl);
        } catch (IOException e) {
            throw FileUploadException.uploadFail(e.getMessage());
        }
    }

    private void validateRequest(FileUploadRequest fileUploadRequest) {
        if (fileUploadRequest.getFile() == null && fileUploadRequest.getFileInputStream() == null) {
            throw FileUploadException.uploadFail("上传文件内容不能为空");
        }
        validateFileName(fileUploadRequest.getFileName());
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw FileUploadException.uploadFail("上传文件名不能为空");
        }
        if (fileName.contains("/") || fileName.contains("\\")) {
            throw FileUploadException.uploadFail("上传文件名不能包含路径分隔符");
        }
    }

    private void validatePathSegment(String pathSegment) {
        if (pathSegment == null || pathSegment.isBlank() || ".".equals(pathSegment) || "..".equals(pathSegment)
                || pathSegment.contains("/") || pathSegment.contains("\\")) {
            throw FileUploadException.uploadFail("非法的文件路径片段: " + pathSegment);
        }
    }

    /**
     * 具体服务器实现的上传方法
     * @param inputStream 文件流
     * @param fileKey 文件唯一key（通常是唯一路径和文件名的拼接）
     * @return 返回文件上传后的唯一Key，不保证可访问性
     * @throws FileUploadException 文件上传异常
     */
    protected abstract String doUploadFile(InputStream inputStream, String fileKey) throws FileUploadException;

}
