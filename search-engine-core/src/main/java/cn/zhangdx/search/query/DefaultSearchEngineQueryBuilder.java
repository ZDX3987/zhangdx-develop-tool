package cn.zhangdx.search.query;

import cn.zhangdx.support.PageQuery;

/**
 *
 * @author ZDX
 * @date 2026/5/7 17:19
 */
public class DefaultSearchEngineQueryBuilder implements SearchEngineQueryBuilder {

    private String queryString;

    private Integer pageSize;

    private Integer currentPage;

    private Class<?> documentType;


    @Override
    public SearchEngineQueryBuilder queryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    @Override
    public SearchEngineQueryBuilder pageable(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public SearchEngineQueryBuilder setDocumentType(Class<?> documentType) {
        this.documentType = documentType;
        return this;
    }

    @Override
    public SearchEngineQuery build() {
        DefaultSearchEngineQuery searchEngineQuery = new DefaultSearchEngineQuery();
        searchEngineQuery.setKeyword(queryString);
        searchEngineQuery.setPageQuery(new PageQuery<>(this.pageSize, this.currentPage));
        searchEngineQuery.setSupportType(documentType);
        return searchEngineQuery;
    }
}
