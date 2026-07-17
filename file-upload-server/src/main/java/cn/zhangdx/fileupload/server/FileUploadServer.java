package cn.zhangdx.fileupload.server;

import cn.zhangdx.fileupload.exception.FileUploadException;
import cn.zhangdx.fileupload.request.FileUploadRequest;
import org.springframework.web.multipart.MultipartFile;

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
     * 获取文件服务器可访问的域名
     * @return 域名的字符创
     */
    String getAccessibleDomain();

}
