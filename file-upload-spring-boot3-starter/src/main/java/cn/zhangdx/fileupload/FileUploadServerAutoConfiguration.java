package cn.zhangdx.fileupload;

import cn.zhangdx.fileupload.processor.FileUploadProcessor;
import cn.zhangdx.fileupload.processor.FileUploadProcessorChain;
import cn.zhangdx.fileupload.processor.GeneralFileNameProcessor;
import cn.zhangdx.fileupload.processor.ImageFileUploadProcessor;
import cn.zhangdx.fileupload.server.FileUploadServer;
import cn.zhangdx.fileupload.server.oss.AliYunOssConfig;
import cn.zhangdx.fileupload.server.oss.AliYunOssFileUploadServer;
import com.aliyun.oss.OSSClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * 文件上次服务自动配置类
 * @author zhangdx
 * @date 2026/7/17 21:57
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "zhangdx.file-upload", name = "enabled",  havingValue = "true")
@EnableConfigurationProperties(FileUploadServerProperties.class)
public class FileUploadServerAutoConfiguration {

    @Bean
    @Order(1)
    @ConditionalOnProperty(prefix = "zhangdx.file-upload", name = "rename",  havingValue = "true")
    public FileUploadProcessor generalFileNameProcessor() {
        return new GeneralFileNameProcessor();
    }

    @Bean
    @Order(2)
    @ConditionalOnProperty(prefix = "zhangdx.file-upload.image-config", name = "only-webp",  havingValue = "true")
    public FileUploadProcessor imageFileUploadProcessor() {
        return new ImageFileUploadProcessor();
    }

    @Bean
    public FileUploadProcessorChain fileUploadProcessorChain(List<FileUploadProcessor> fileUploadProcessors) {
        FileUploadProcessorChain fileUploadProcessorChain = new FileUploadProcessorChain();
        fileUploadProcessorChain.batchAddProcessor(fileUploadProcessors);
        return fileUploadProcessorChain;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(OSSClient.class)
    @ConditionalOnProperty(prefix = "zhangdx.file-upload", name = "vendor", matchIfMissing = true)
    static class AliYunOssConfiguration {

        @Bean
        public FileUploadServer aliYunOssFileUploadServer(FileUploadServerProperties fileUploadServerProperties, FileUploadProcessorChain fileUploadProcessorChain) {
            FileUploadServerProperties.AliYunOssProperties aliYunOssProperties = fileUploadServerProperties.getAliyun();
            AliYunOssConfig aliYunOssConfig = AliYunOssConfig.builder().endpoint(aliYunOssProperties.getEndpoint())
                    .bucketName(aliYunOssProperties.getBucketName()).accessKeyId(aliYunOssProperties.getAccessKeyId())
                    .accessKeySecret(aliYunOssProperties.getAccessKeySecret())
                    .proxyDomain(aliYunOssProperties.getProxyDomain()).build();
            return new AliYunOssFileUploadServer(aliYunOssConfig, fileUploadProcessorChain);
        }
    }
}
