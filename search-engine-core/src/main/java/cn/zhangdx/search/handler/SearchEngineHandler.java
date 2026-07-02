package cn.zhangdx.search.handler;

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
}
