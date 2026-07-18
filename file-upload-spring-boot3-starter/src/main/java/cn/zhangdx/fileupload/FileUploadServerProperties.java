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
    private boolean enabled;

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
        /** WebP 压缩质量，范围为 0 到 1。 */
        private float compressionQuality = 0.75f;
        /** 允许解码的最大图片像素数，防止超大图片占用过多内存。 */
        private long maxPixels = 20_000_000L;
        /** 同时执行图片转换的最大任务数，用于限制 JVM 峰值内存。 */
        private int maxConcurrentConversions = 2;
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
