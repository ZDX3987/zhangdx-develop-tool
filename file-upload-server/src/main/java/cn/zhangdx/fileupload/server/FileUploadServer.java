package cn.zhangdx.fileupload.server;

import cn.zhangdx.fileupload.exception.FileUploadException;
import cn.zhangdx.fileupload.request.FileUploadRequest;
import cn.zhangdx.fileupload.result.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 文件上传服务
 * @author zhangdx
 * @date 2026/5/14 12:47
 */
public interface FileUploadServer {

    /**
     * 文件上传
     * @param file 文件
     * @return 返回文件的路径
     * @throws FileUploadException 文件上传异常
     */
    String uploadFile(MultipartFile file) throws FileUploadException;

    /**
     * 文件上传
     *
     * @param fileUploadRequest 文件上传请求参数
     * @return 返回文件的路径
     * @throws FileUploadException 文件上传异常
     */
    String uploadFile(FileUploadRequest fileUploadRequest) throws FileUploadException;

    /**
     * 上传文件并返回结构化结果。默认实现用于兼容已有 FileUploadServer 实现，建议实现类覆盖以提供 fileKey。
     */
    default FileUploadResult uploadFileWithResult(FileUploadRequest fileUploadRequest) throws FileUploadException {
        return new FileUploadResult(null, uploadFile(fileUploadRequest));
    }

    default FileUploadResult uploadFileWithResult(MultipartFile file) throws FileUploadException {
        return uploadFileWithResult(FileUploadRequest.build(file, List.of()));
    }

    /**
     * 上传输入流，适用于非 Web 场景。输入流由上传服务在上传完成后关闭。
     */
    default String uploadFile(InputStream inputStream, String fileName, List<String> filePath)
            throws FileUploadException {
        return uploadFile(FileUploadRequest.build(inputStream, fileName, filePath));
    }

    /**
     * 获取文件服务器可访问的域名
     * @return 域名的字符创
     */
    String getAccessibleDomain();

}
