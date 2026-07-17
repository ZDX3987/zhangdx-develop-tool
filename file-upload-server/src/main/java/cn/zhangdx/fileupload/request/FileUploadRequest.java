package cn.zhangdx.fileupload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 文件上传请求参数
 * @author zhangdx
 * @date 2026/5/14 13:52
 */
@Data
public class FileUploadRequest {

    private MultipartFile file;

    private String fileName;

    private List<String> filePath;

    private InputStream fileInputStream;

    public static FileUploadRequest build(MultipartFile file, List<String> filePath) {
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFile(file);
        fileUploadRequest.setFilePath(filePath);
        fileUploadRequest.setFileName(file.getOriginalFilename());
        return fileUploadRequest;
    }

    public InputStream openFileInputStream() throws IOException {
        if (fileInputStream == null) {
            fileInputStream = this.file.getInputStream();
        }
        return fileInputStream;
    }
}
