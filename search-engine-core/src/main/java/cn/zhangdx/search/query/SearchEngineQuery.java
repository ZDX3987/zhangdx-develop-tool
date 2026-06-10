package cn.zhangdx.search.query;

import cn.zhangdx.support.PageQuery;

/**
 *
 * @author ZDX
 * @date 2026/5/6 23:17
 */
public interface SearchEngineQuery {

    <E> Class<E> getSupportType();

    String getQueryString();

    String getDocumentIndex();

    PageQuery<?> getPageQuery();

    String[] getHighlightFields();

}
