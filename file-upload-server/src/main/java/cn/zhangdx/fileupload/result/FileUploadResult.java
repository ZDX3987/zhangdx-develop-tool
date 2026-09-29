package cn.zhangdx.fileupload.result;

/**
 * 文件上传结果。
 * @param fileName 文件名
 * @param fileKey 文件服务器中的对象 key
 * @param accessibleUrl 文件可访问地址
 */
public record FileUploadResult(String fileName, String fileKey, String accessibleUrl) {
}
