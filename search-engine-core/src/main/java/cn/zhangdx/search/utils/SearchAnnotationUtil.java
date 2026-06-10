package cn.zhangdx.search.utils;

import cn.zhangdx.search.annotation.SearchDocument;
import cn.zhangdx.search.annotation.SearchHighlight;
import cn.zhangdx.search.annotation.SearchPrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索读取枚举的工具类
 * @author zhangdx
 * @date 2026/5/9 10:59
 */
public class SearchAnnotationUtil {

    public static String parseIndexName(Class<?> documentType) {
        SearchDocument searchDocumentAnnotation = documentType.getAnnotation(SearchDocument.class);
        if (searchDocumentAnnotation == null) {
            throw new IllegalArgumentException("search document type " + documentType.getName() +" must have @SearchDocument annotation");
        }
        return searchDocumentAnnotation.indexName();
    }

    public static String parsePrimaryKeyFromField(Class<?> documentType) {
        String primaryKey = null;
        for (Field declaredField : documentType.getDeclaredFields()) {
            SearchPrimaryKey searchPrimaryKeyAnnotation = declaredField.getAnnotation(SearchPrimaryKey.class);
            if (searchPrimaryKeyAnnotation == null) {
                continue;
            }
            primaryKey = declaredField.getName();
        }
        if (primaryKey == null) {
            throw new IllegalArgumentException("search document type " + documentType.getName() +" must have @SearchPrimaryKey annotation");
        }
        return primaryKey;
    }

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
