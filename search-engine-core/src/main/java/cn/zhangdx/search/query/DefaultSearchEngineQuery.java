package cn.zhangdx.search.query;

import cn.zhangdx.search.utils.SearchAnnotationUtil;
import cn.zhangdx.support.PageQuery;
import lombok.Data;

/**
 *
 * @author ZDX
 * @date 2026/5/6 23:57
 */
@Data
public class DefaultSearchEngineQuery implements SearchEngineQuery {

    private Class<?> supportType;

    private String keyword;

    private PageQuery<?> pageQuery;

    private String documentIndex;

    private String[] highlightFields;

    public DefaultSearchEngineQuery() {}

    public DefaultSearchEngineQuery(Class<?> supportType) {
        this.supportType = supportType;
    }

    @Override
    public String getQueryString() {
        return keyword;
    }

    @Override
    public String getDocumentIndex() {
        if (this.documentIndex == null) {
            this.documentIndex = SearchAnnotationUtil.parseIndexName(this.supportType);
        }
        return this.documentIndex;
    }

    @Override
    public String[] getHighlightFields() {
        if (this.highlightFields == null) {
            this.highlightFields = SearchAnnotationUtil.parseHighlightField(this.supportType);
        }
        return this.highlightFields;
    }

    public static DefaultSearchEngineQueryBuilder builder() {
        return new DefaultSearchEngineQueryBuilder();
    }
}
