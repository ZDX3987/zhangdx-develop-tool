package cn.zhangdx.search.query;

import cn.zhangdx.search.utils.SearchAnnotationUtil;
import lombok.Getter;

/**
 *
 * @author ZDX
 * @date 2026/5/8 23:19
 */
@Getter
public class SearchEngineSaveRequest<E> {

    private Class<E> documentType;

    private String indexName;

    private String primaryKey;

    private Boolean refresh;


    public SearchEngineSaveRequest(Class<E> documentType, boolean refresh) {
        this.documentType = documentType;
        this.indexName = SearchAnnotationUtil.parseIndexName(documentType);
        this.primaryKey = SearchAnnotationUtil.parsePrimaryKeyFromField(documentType);
        this.refresh = refresh;
    }

}
