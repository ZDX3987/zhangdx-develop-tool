package cn.zhangdx.search.query;

/**
 *
 * @author ZDX
 * @date 2026/5/6 23:47
 */
public interface SearchEngineQueryBuilder {

    SearchEngineQueryBuilder queryString(String queryString);

    SearchEngineQueryBuilder pageable(int currentPage, int pageSize);

    SearchEngineQueryBuilder setDocumentType(Class<?> documentType);

    SearchEngineQuery build();
}
