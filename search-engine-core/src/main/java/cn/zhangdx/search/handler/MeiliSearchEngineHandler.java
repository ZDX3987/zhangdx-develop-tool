package cn.zhangdx.search.handler;

import cn.zhangdx.search.converter.SearchEngineConverterManager;
import cn.zhangdx.search.query.SearchEngineQuery;
import cn.zhangdx.search.query.SearchEngineSaveRequest;
import cn.zhangdx.support.PageQuery;
import cn.zhangdx.support.ResultPage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResultPaginated;
import com.meilisearch.sdk.model.Searchable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * MeiliSearch支持的搜索引擎处理类
 * @author ZDX
 * @date 2026/5/7 15:25
 */
public class MeiliSearchEngineHandler extends AbstractSearchEngineHandler {

    private final Client client;

    public MeiliSearchEngineHandler(Client client, SearchEngineConverterManager searchEngineConverterManager) {
        super(searchEngineConverterManager);
        this.client = client;
    }

    /**
     * 批量存储文档数据
     *
     * @param documents   文档数据
     * @param saveRequest
     */
    @Override
    public <E> void batchSaveDocument(Collection<E> documents, SearchEngineSaveRequest<E> saveRequest) {
        Index index = client.index(saveRequest.getIndexName());
        if (Boolean.TRUE.equals(saveRequest.getRefresh())) {
            index.deleteAllDocuments();
        }
        index.addDocumentsInBatches(JSONArray.toJSONString(documents, SerializerFeature.DisableCircularReferenceDetect),
                100, saveRequest.getPrimaryKey());
    }

    /**
     * 关键字查询分页内容
     *
     * @param searchEngineQuery 搜索参数
     * @return 返回搜索到的内容
     */
    @Override
    public ResultPage<?> searchDocumentPage(SearchEngineQuery searchEngineQuery) {
        Index index = client.index(searchEngineQuery.getDocumentIndex());
        PageQuery<?> pageQuery = searchEngineQuery.getPageQuery();
        SearchRequest searchRequest = this.genSearchRequest(searchEngineQuery);
        Searchable searchable = index.search(searchRequest);
        ResultPage<?> resultPage = new ResultPage<>(pageQuery.getCurrent(), pageQuery.getPageSize());
        List<?> sourceRecords = JSONArray.parseArray(JSONObject.toJSONString(getFormattedHits(searchable)), searchEngineQuery.getSupportType());
        resultPage.setRecords(this.applyConverter(sourceRecords));
        if (searchable instanceof SearchResultPaginated resultPaginated) {
            resultPage.setTotal(resultPaginated.getTotalHits());
        }
        return resultPage;
    }

    /**
     * 存储文档数据
     *
     * @param document    文档数据
     * @param saveRequest 保存参数
     */
    @Override
    public <E> void saveDocument(E document, SearchEngineSaveRequest<E> saveRequest) {
        Index index = client.index(saveRequest.getIndexName());
        if (Boolean.TRUE.equals(saveRequest.getRefresh())) {
            index.deleteAllDocuments();
        }
        index.addDocuments(JSONArray.toJSONString(document, SerializerFeature.DisableCircularReferenceDetect),
                 saveRequest.getPrimaryKey());
    }

    @Override
    protected <E> List<E> doSearchDocument(SearchEngineQuery searchEngineQuery) {
        Index index = client.index(searchEngineQuery.getDocumentIndex());
        SearchRequest searchRequest = this.genSearchRequest(searchEngineQuery);
        Searchable searchable = index.search(searchRequest);
        return JSONArray.parseArray(JSONObject.toJSONString(searchable.getHits()), searchEngineQuery.getSupportType());
    }

    private SearchRequest genSearchRequest(SearchEngineQuery searchEngineQuery) {
        PageQuery<?> pageQuery = searchEngineQuery.getPageQuery();
        return SearchRequest.builder().q(searchEngineQuery.getQueryString())
                .highlightPreTag("<span class='search-keyword-highlight'>").highlightPostTag("</span>")
                .attributesToHighlight(searchEngineQuery.getHighlightFields())
                .page((int) pageQuery.getCurrent()).hitsPerPage((int) pageQuery.getPageSize())
                .build();
    }

    private List<Object> getFormattedHits(Searchable searchable) {
        ArrayList<HashMap<String, Object>> hits = searchable.getHits();
        return hits.stream().map(subMap -> subMap.getOrDefault("_formatted", subMap)).toList();
    }
}
