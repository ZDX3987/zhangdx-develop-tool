package cn.zhangdx.fileupload.server.oss;

import lombok.Data;

/**
 * 阿里云OSS配置
 * @author zhangdx
 * @date 2026/7/17 22:30
 */
@Data
public class AliYunOssConfig {
    private String endpoint;
    private String bucketName;
    private String accessKeyId;
    private String accessKeySecret;
    private String proxyDomain;
}
