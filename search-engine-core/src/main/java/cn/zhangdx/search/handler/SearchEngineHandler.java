package cn.zhangdx.search.handler;

import cn.zhangdx.search.exception.SearchEngineServerException;
import cn.zhangdx.search.query.SearchEngineQuery;
import cn.zhangdx.search.query.SearchEngineSaveRequest;
import cn.zhangdx.support.pagination.ResultPage;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author ZDX
 * @date 2026/5/6 22:37
 */
public interface SearchEngineHandler {

    /**
     * 使用关键字搜索
     * @param searchEngineQuery 搜索参数
     * @return 返回搜索到的内容
     */
    <E> List<E> searchDocument(SearchEngineQuery searchEngineQuery);

    /**
     * 关键字查询分页内容
     *
     * @param searchEngineQuery 搜索参数
     * @return 返回搜索到的内容
     */
    ResultPage<?> searchDocumentPage(SearchEngineQuery searchEngineQuery);

    /**
     * 存储文档数据
     *
     * @param <E>         文档数据类型
     * @param document   文档数据
     * @param saveRequest 保存参数
     */
    <E> void saveDocument(E document, SearchEngineSaveRequest<E> saveRequest);

    /**
     * 批量存储文档数据
     *
     * @param <E>         文档数据类型
     * @param documents   文档数据
     * @param saveRequest 保存参数
     */
    <E> void batchSaveDocument(Collection<E> documents, SearchEngineSaveRequest<E> saveRequest);

    /**
     * 删除单个文档
     *
     * @param documentItem 文档内容
     * @param <E>          文档数据类型
     * @throws SearchEngineServerException 搜索引擎服务端异常
     */
    <E> void deleteDocument(E documentItem) throws SearchEngineServerException;

    /**
     * 删除指定索引下的文档
     *
     * @param indexName  索引名称
     * @param primaryKey 主键key
     * @throws SearchEngineServerException 搜索引擎服务端异常
     */
    void deleteDocument(String indexName, String primaryKey) throws SearchEngineServerException;
}
