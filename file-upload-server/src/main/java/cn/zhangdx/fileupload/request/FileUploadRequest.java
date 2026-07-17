package cn.zhangdx.fileupload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * 文件上传请求参数
 * @author zhangdx
 * @date 2026/5/14 13:52
 */
@Data
public class FileUploadRequest implements AutoCloseable {

    private MultipartFile file;

    private String fileName;

    private List<String> filePath;

    private InputStream fileInputStream;

    public static FileUploadRequest build(MultipartFile file, List<String> filePath) {
        Objects.requireNonNull(file, "file must not be null");
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFile(file);
        fileUploadRequest.setFilePath(filePath == null ? List.of() : List.copyOf(filePath));
        fileUploadRequest.setFileName(file.getOriginalFilename());
        return fileUploadRequest;
    }

    /**
     * 构建不依赖 Web MultipartFile 的上传请求。输入流由上传服务在上传完成后关闭。
     */
    public static FileUploadRequest build(InputStream inputStream, String fileName, List<String> filePath) {
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFileInputStream(Objects.requireNonNull(inputStream, "inputStream must not be null"));
        fileUploadRequest.setFileName(fileName);
        fileUploadRequest.setFilePath(filePath == null ? List.of() : List.copyOf(filePath));
        return fileUploadRequest;
    }

    public InputStream openFileInputStream() throws IOException {
        if (fileInputStream == null) {
            if (file == null) {
                throw new IOException("没有可读取的上传文件内容");
            }
            fileInputStream = this.file.getInputStream();
        }
        return fileInputStream;
    }

    @Override
    public void close() throws IOException {
        if (fileInputStream != null) {
            fileInputStream.close();
        }
    }
}
