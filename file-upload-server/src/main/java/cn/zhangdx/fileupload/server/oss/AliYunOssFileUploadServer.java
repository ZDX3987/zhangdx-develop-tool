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
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
            return fileKey;
        } catch (ClientException | OSSException e) {
            log.error("uploadFile oss error: ", e);
            throw FileUploadException.uploadFail("阿里云OSS文件上传异常");
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (ossClient == null) {
            ossClient = buildOSSClient();
        }
    }

    @Override
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
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

    /**
     * 删除单个文件
     *
     * @param fileKey 文件唯一Key
     * @return 结果
     */
    @Override
    public boolean deleteFile(String fileKey) throws FileUploadException {
        log.info("deleteFile fileKey: {}", fileKey);
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(aliYunOssConfig.getBucketName());
        try {
            ossClient.deleteObject(deleteObjectsRequest);
            return true;
        } catch (OSSException | ClientException e) {
            log.error("deleteFile oss error: ", e);
            throw FileUploadException.uploadFail("文件删除失败");
        }
    }

    /**
     * 批量删除多个文件
     *
     * @param fileKeys 文件Key集合
     * @return 结果
     */
    @Override
    public boolean batchDeleteFiles(Collection<String> fileKeys) throws FileUploadException {
        log.info("batchDeleteFiles fileKeys: {}", fileKeys);
        if (fileKeys.isEmpty()) {
            throw FileUploadException.uploadFail("文件Key不能为空");
        }
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(aliYunOssConfig.getBucketName())
                .withKeys(new ArrayList<>(fileKeys));
        try {
            DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(deleteObjectsRequest);
            return Objects.equals(deleteObjectsResult.getDeletedObjects().size(), fileKeys.size());
        } catch (OSSException | ClientException e) {
            log.error("batchDeleteFiles oss error: ", e);
            throw FileUploadException.uploadFail("文件删除失败");
        }
    }

    /**
     * 删除某个路径
     *
     * @param directory   路径
     * @param forceDelete 是否强制删除（忽略内部文件）
     * @return 结果
     */
    @Override
    public boolean deleteDirectory(String directory, boolean forceDelete) throws FileUploadException {
        log.info("deleteFileByDirectory directory: {}, forceDelete: {}", directory, forceDelete);
        if (!StringUtils.hasText(directory)) {
            throw FileUploadException.uploadFail("文件目录不能为空");
        }
        String bucketName = aliYunOssConfig.getBucketName();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName).withPrefix(directory);
        ObjectListing objectListResult = ossClient.listObjects(listObjectsRequest);
        List<String> keyList = objectListResult.getObjectSummaries().stream().map(OSSObjectSummary::getKey).toList();
        if (keyList.isEmpty()) {
            return false;
        }
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keyList);
        try {
            DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(deleteObjectsRequest);
            return !deleteObjectsResult.getDeletedObjects().isEmpty();
        } catch (OSSException | ClientException e) {
            log.error("deleteDirectory oss error: ", e);
            throw FileUploadException.uploadFail("文件删除失败");
        }
    }

    private OSS buildOSSClient() {
        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
        configuration.setProtocol(Protocol.HTTPS);
        return new OSSClientBuilder().build(aliYunOssConfig.getEndpoint(),
                aliYunOssConfig.getAccessKeyId(), aliYunOssConfig.getAccessKeySecret(), configuration);
    }
}
