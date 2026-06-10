package cn.zhangdx.search.handler;

import cn.zhangdx.search.converter.SearchEngineConverterManager;
import cn.zhangdx.search.query.SearchEngineQuery;
import cn.zhangdx.search.query.SearchEngineSaveRequest;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;

import java.util.Collection;
import java.util.List;

/**
 * Elasticsearch支持的搜索引擎处理类
 * @author ZDX
 * @date 2026/5/6 22:41
 */
public class EsSearchEngineHandler extends AbstractSearchEngineHandler {

    private final ElasticsearchOperations elasticsearchOperations;

    public EsSearchEngineHandler(ElasticsearchOperations elasticsearchOperations, SearchEngineConverterManager searchEngineConverterManager) {
        super(searchEngineConverterManager);
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    protected <E> List<E> doSearchDocument(SearchEngineQuery searchEngineQuery) {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.queryString(qb -> qb.query(searchEngineQuery.getQueryString())));
        SearchHits<E> searchHits = elasticsearchOperations.search(queryBuilder.build(), searchEngineQuery.getSupportType());
        return searchHits.getSearchHits().stream().map(SearchHit::getContent).toList();
    }

    /**
     * 存储文档数据
     *
     * @param document    文档数据
     * @param saveRequest 保存参数
     */
    @Override
    public <E> void saveDocument(E document, SearchEngineSaveRequest<E> saveRequest) {
        IndexCoordinates indexCoordinates = IndexCoordinates.of(saveRequest.getIndexName());
        if (Boolean.TRUE.equals(saveRequest.getRefresh())) {
            Query query = new StringQuery(StringQuery.MATCH_ALL);
            elasticsearchOperations.delete(query, document.getClass(), indexCoordinates);
        }
        elasticsearchOperations.save(document, indexCoordinates);
    }

    /**
     * 批量存储文档数据
     *
     * @param documents   文档数据
     * @param saveRequest 保存参数
     */
    @Override
    public <E> void batchSaveDocument(Collection<E> documents, SearchEngineSaveRequest<E> saveRequest) {
        IndexCoordinates indexCoordinates = IndexCoordinates.of(saveRequest.getIndexName());
        if (Boolean.TRUE.equals(saveRequest.getRefresh())) {
            Query query = new StringQuery(StringQuery.MATCH_ALL);
            Object[] documentsArray = documents.toArray();
            Object object = documentsArray[0];
            Class<?> aClass = object.getClass();
            elasticsearchOperations.delete(query, aClass, indexCoordinates);
        }
        elasticsearchOperations.save(documents, indexCoordinates);
    }
}
