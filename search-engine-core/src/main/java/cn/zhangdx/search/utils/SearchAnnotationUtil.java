package cn.zhangdx.search.utils;

import cn.zhangdx.search.annotation.SearchDocument;
import cn.zhangdx.search.annotation.SearchHighlight;
import cn.zhangdx.search.annotation.SearchPrimaryKey;
import cn.zhangdx.search.exception.SearchEngineRequestException;
import cn.zhangdx.search.query.SearchDocumentTypeMetadata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索读取枚举的工具类
 * @author zhangdx
 * @date 2026/5/9 10:59
 */
public class SearchAnnotationUtil {

    /**
     * 解析搜索文档类型中定义的文档索引名称
     * @param documentType 搜索文档类型
     * @return 索引名称
     * @throws SearchEngineRequestException 当解析异常或没有定义索引时抛出该异常
     */
    public static String parseIndexName(Class<?> documentType) throws SearchEngineRequestException {
        SearchDocument searchDocumentAnnotation = documentType.getAnnotation(SearchDocument.class);
        if (searchDocumentAnnotation == null) {
            throw new SearchEngineRequestException("Search document type " + documentType.getName() +" must has field with @SearchDocument annotation");
        }
        return searchDocumentAnnotation.indexName();
    }

    /**
     * 解析搜索文档类型中定义文档主键的属性名
     * @param documentType 搜索文档类型
     * @return 作为主键的属性名
     * @throws SearchEngineRequestException 当解析异常或没有定义主键属性时抛出该异常
     */
    public static String parsePrimaryKeyFromField(Class<?> documentType) throws SearchEngineRequestException {
        String primaryKey = null;
        for (Field declaredField : documentType.getDeclaredFields()) {
            SearchPrimaryKey searchPrimaryKeyAnnotation = declaredField.getAnnotation(SearchPrimaryKey.class);
            if (searchPrimaryKeyAnnotation == null) {
                continue;
            }
            primaryKey = declaredField.getName();
        }
        if (primaryKey == null) {
            throw new SearchEngineRequestException("Search document type " + documentType.getName() +" must has field with @SearchPrimaryKey annotation");
        }
        return primaryKey;
    }

    /**
     * 解析文档类型中声明的元数据信息
     * @param documentType 文档类型
     * @return 元数据信息
     * @throws SearchEngineRequestException 解析异常
     */
    public static SearchDocumentTypeMetadata parseDocumentTypeMetadata(Class<?> documentType) throws SearchEngineRequestException {
        return new SearchDocumentTypeMetadata(parseIndexName(documentType), parsePrimaryKeyFromField(documentType));
    }

    /**
     * 解析搜索文档类型中定义高亮的字段名称数组（可能存在多个）
     * @param documentType 搜索文档类型
     * @return 定义高亮的字段名称数组
     */
    public static String[] parseHighlightField(Class<?> documentType) {
        List<String> highlightFields = new ArrayList<>();
        for (Field declaredField : documentType.getDeclaredFields()) {
            SearchHighlight searchHighlightAnnotation = declaredField.getAnnotation(SearchHighlight.class);
            if (searchHighlightAnnotation == null) {
                continue;
            }
            highlightFields.add(declaredField.getName());
        }
        return highlightFields.toArray(new String[0]);
    }

}
