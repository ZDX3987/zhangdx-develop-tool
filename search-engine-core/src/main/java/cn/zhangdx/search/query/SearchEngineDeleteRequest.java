package cn.zhangdx.search.query;


/**
 * 删除文档对象参数
 * @author zhangdx
 * @date 2026/9/16 22:18
 */
public record SearchEngineDeleteRequest(String indexName, String primaryKey) {
}
