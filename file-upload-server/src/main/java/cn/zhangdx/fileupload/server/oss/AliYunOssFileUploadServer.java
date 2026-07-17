package cn.zhangdx.fileupload.server.oss;

import cn.zhangdx.fileupload.exception.FileUploadException;
import cn.zhangdx.fileupload.processor.FileUploadProcessorChain;
import cn.zhangdx.fileupload.server.AbstractFileUploadServer;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.comm.Protocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.InputStream;

/**
 * 阿里云OSS上传服务
 * @author zhangdx
 * @date 2026/5/14 12:55
 */
@Slf4j
public class AliYunOssFileUploadServer extends AbstractFileUploadServer implements InitializingBean, DisposableBean {

    private final AliYunOssConfig aliYunOssConfig;

    private OSS ossClient;

    public AliYunOssFileUploadServer(AliYunOssConfig aliYunOssConfig, FileUploadProcessorChain fileUploadProcessorChain) {
        super(fileUploadProcessorChain);
        this.aliYunOssConfig = aliYunOssConfig;
    }

    @Override
    protected String doUploadFile(InputStream inputStream, String fileKey) {
        log.info("oss client doUploadFile fileKey:{}", fileKey);
        try {
            ossClient.putObject(aliYunOssConfig.getBucketName(), fileKey, inputStream);
            return getAccessibleDomain().replaceAll("/+$", "") + "/" + fileKey;
        } catch (ClientException | OSSException e) {
            log.error("uploadFile oss error: ", e);
            throw FileUploadException.uploadFail("阿里云OSS文件上传异常");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (ossClient == null) {
            ossClient = buildOSSClient();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    private OSS buildOSSClient() {
        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
        configuration.setProtocol(Protocol.HTTPS);
        return new OSSClientBuilder().build(aliYunOssConfig.getEndpoint(),
                aliYunOssConfig.getAccessKeyId(), aliYunOssConfig.getAccessKeySecret(), configuration);
    }

    /**
     * 获取文件服务器可访问的域名
     *
     * @return 域名的字符创
     */
    @Override
    public String getAccessibleDomain() {
        return aliYunOssConfig.getProxyDomain();
    }
}
