package cn.zhangdx.fileupload.server;

import cn.zhangdx.fileupload.exception.FileUploadException;

import java.util.Collection;

/**
 * 文件删除接口
 * @author ZDX
 * @date 2026/7/18 18:07
 */
public interface FileDelete {

    /**
     * 删除单个文件
     * @param fileKey 文件唯一Key
     * @return 结果
     */
    boolean deleteFile(String fileKey) throws FileUploadException;

    /**
     * 批量删除多个文件
     * @param fileKeys 文件Key集合
     * @return 结果
     */
    boolean batchDeleteFiles(Collection<String> fileKeys) throws FileUploadException;

    /**
     * 删除某个路径
     * @param directory 路径
     * @param forceDelete 是否强制删除（忽略内部文件）
     * @return 是否删除文件
     */
    boolean deleteDirectory(String directory, boolean forceDelete) throws FileUploadException;
}
