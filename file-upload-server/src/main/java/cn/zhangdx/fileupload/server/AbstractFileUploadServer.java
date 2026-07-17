package cn.zhangdx.fileupload.server;

import cn.zhangdx.fileupload.exception.FileUploadException;
import cn.zhangdx.fileupload.processor.FileUploadProcessorChain;
import cn.zhangdx.fileupload.request.FileUploadRequest;
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
        if (fileUploadProcessorChain != null) {
            fileUploadProcessorChain.doProcess(fileUploadRequest);
        }
        List<String> filePathList = new ArrayList<>(fileUploadRequest.getFilePath());
        filePathList.add(fileUploadRequest.getFileName());
        String fullFilePath = String.join("/", filePathList);
        try (InputStream inputStream = fileUploadRequest.openFileInputStream()) {
            return doUploadFile(inputStream, fullFilePath);
        } catch (IOException e) {
            throw FileUploadException.uploadFail(e.getMessage());
        }
    }

    /**
     * 具体服务器实现的上传方法
     * @param inputStream 文件流
     * @param fileKey 文件唯一key（通常是唯一路径和文件名的拼接）
     * @return 返回文件可访问路径
     * @throws FileUploadException 文件上传异常
     */
    protected abstract String doUploadFile(InputStream inputStream, String fileKey) throws FileUploadException;

}