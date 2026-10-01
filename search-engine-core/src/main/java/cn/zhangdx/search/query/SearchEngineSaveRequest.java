package cn.zhangdx.search.query;

import cn.zhangdx.search.exception.SearchEngineRequestException;
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

    private SearchDocumentTypeMetadata documentTypeMetadata;

    private Boolean refresh;


    public SearchEngineSaveRequest(Class<E> documentType, boolean refresh) throws SearchEngineRequestException {
        this.documentType = documentType;
        this.documentTypeMetadata = SearchAnnotationUtil.parseDocumentTypeMetadata(documentType);
        this.refresh = refresh;
    }

}
