package cn.zhangdx.fileupload;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件上传服务配置属性
 * @author zhangdx
 * @date 2026/7/17 21:59
 */
@Data
@ConfigurationProperties(prefix = "zhangdx.file-upload")
public class FileUploadServerProperties {

    /**
     * 是佛开启使用文件上传
     */
    private boolean enabled = true;

    private Vendor vendor = Vendor.AliYun;

    private AliYunOssProperties aliyun = new AliYunOssProperties();

    private boolean rename;

    private ImageConfig imageConfig = new ImageConfig() ;

    public enum Vendor {
        AliYun,
    }

    /**
     * 图片文件配置
     */
    @Data
    public static class ImageConfig {
        private boolean onlyWebp;
    }

    @Data
    static class AliYunOssProperties {
        private String endpoint;
        private String bucketName;
        private String accessKeyId;
        private String accessKeySecret;
        private String proxyDomain;
    }
}
