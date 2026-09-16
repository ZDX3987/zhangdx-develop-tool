package cn.zhangdx.search.handler;

import cn.zhangdx.search.converter.SearchEngineConverterManager;
import cn.zhangdx.search.exception.SearchEngineException;
import cn.zhangdx.search.query.SearchEngineDeleteRequest;
import cn.zhangdx.search.query.SearchEngineQuery;
import cn.zhangdx.support.pagination.PageQuery;
import cn.zhangdx.support.pagination.ResultPage;
import cn.zhangdx.support.util.StringUtil;

import java.util.List;

/**
 * 搜索引擎处理器抽象类
 * @author zhangdx
 * @date 2026/5/9 07:51
 */
public abstract class AbstractSearchEngineHandler implements SearchEngineHandler {

    private final SearchEngineConverterManager searchEngineConverterManager;

    public AbstractSearchEngineHandler(SearchEngineConverterManager searchEngineConverterManager) {
        this.searchEngineConverterManager = searchEngineConverterManager;
    }

    /**
     * 使用关键字搜索
     *
     * @param searchEngineQuery 搜索参数
     * @return 返回搜索到的内容
     */
    @Override
    public <E> List<E> searchDocument(SearchEngineQuery searchEngineQuery) {
        List<E> document = this.doSearchDocument(searchEngineQuery);
        return this.applyConverter(document);
    }

    /**
     * 关键字查询分页内容
     *
     * @param searchEngineQuery 搜索参数
     * @return 返回搜索到的内容
     */
    @Override
    public ResultPage<?> searchDocumentPage(SearchEngineQuery searchEngineQuery) {
        PageQuery<?> pageQuery = searchEngineQuery.getPageQuery();
        ResultPage<?> page = pageQuery.buildPage();
        List<?> records = this.searchDocument(searchEngineQuery);
        page.setRecords(this.applyConverter(records));
        return page;
    }

    /**
     * 删除某个文档
     *
     * @param deleteRequest 删除文档参数
     */
    @Override
    public void deleteDocument(SearchEngineDeleteRequest deleteRequest) {
        String indexName = deleteRequest.indexName();
        String primaryKey = deleteRequest.primaryKey();
        if (StringUtil.isEmpty(indexName) || StringUtil.isEmpty(primaryKey)) {
            throw new IllegalArgumentException("删除参数不能为空");
        }
        doDeleteDocument(indexName, primaryKey);
    }

    protected abstract <E> List<E> doSearchDocument(SearchEngineQuery searchEngineQuery);

    protected <E> List<E> applyConverter(List<?> records) {
        return searchEngineConverterManager.batchConvert(records);
    }

    /**
     * 具体删除文档对象方法
     * @param indexName 文档所属的索引
     * @param primaryKey 删除的文档主键
     */
    protected abstract void doDeleteDocument(String indexName, String primaryKey) throws SearchEngineException;

}
