package cn.zhangdx.search.handler;

import cn.zhangdx.search.converter.SearchEngineConverterManager;
import cn.zhangdx.search.query.SearchEngineQuery;
import cn.zhangdx.support.PageQuery;
import cn.zhangdx.support.ResultPage;

import java.util.List;

/**
 * 搜索引擎处理器抽象类
 * @author zhangdx
 * @date 2026/5/9 07:51
 */
public abstract class AbstractSearchEngineHandler implements SearchEngineHandler {

    private SearchEngineConverterManager searchEngineConverterManager;

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
        return this.doSearchDocument(searchEngineQuery);
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

    protected abstract <E> List<E> doSearchDocument(SearchEngineQuery searchEngineQuery);

    protected <E> List<E> applyConverter(List<?> records) {
        return searchEngineConverterManager.batchConvert(records);
    }

}
