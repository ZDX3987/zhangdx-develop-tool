package cn.zhangdx.search;

import cn.zhangdx.search.converter.SearchEngineConverter;
import cn.zhangdx.search.converter.SearchEngineConverterManager;
import cn.zhangdx.search.handler.EsSearchEngineHandler;
import cn.zhangdx.search.handler.MeiliSearchEngineHandler;
import cn.zhangdx.search.handler.SearchEngineHandler;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.util.List;

/**
 * 搜索引擎自动配置类
 * @author zhangdx
 * @date 2026/6/10 19:41
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "zhangdx.search-engine", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({SearchEngineProperties.class})
public class SearchEngineAutoConfiguration {

    @Bean
    @ConditionalOnClass(Client.class)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "zhangdx.search-engine", name = "type", havingValue = "meili-search", matchIfMissing = true)
    public Client meiliSearchClient(SearchEngineProperties searchEngineProperties) {
        SearchEngineProperties.Meili meili = searchEngineProperties.getMeili();
        return new Client(new Config(meili.getHost(), meili.getApiKey()));
    }

    @Bean
    @ConditionalOnMissingBean
    public SearchEngineConverterManager searchEngineConverterManager(List<SearchEngineConverter<?, ?>> searchEngineConverters) {
        SearchEngineConverterManager searchEngineConverterManager = new SearchEngineConverterManager();
        searchEngineConverterManager.setConverterList(searchEngineConverters);
        return searchEngineConverterManager;
    }

    @Bean
    @ConditionalOnBean(Client.class)
    @ConditionalOnMissingBean(SearchEngineHandler.class)
    @ConditionalOnProperty(prefix = "zhangdx.search-engine", name = "type", havingValue = "meili-search", matchIfMissing = true)
    public SearchEngineHandler meiliSearchEngineHandler(Client meiliSearchClient, SearchEngineConverterManager searchEngineConverterManager) {
        return new MeiliSearchEngineHandler(meiliSearchClient, searchEngineConverterManager);
    }

    @Bean
    @ConditionalOnBean(ElasticsearchOperations.class)
    @ConditionalOnMissingBean(SearchEngineHandler.class)
    @ConditionalOnProperty(prefix = "zhangdx.search-engine", name = "type", havingValue = "elasticsearch")
    public SearchEngineHandler esSearchEngineHandler(ElasticsearchOperations elasticsearchOperations, SearchEngineConverterManager searchEngineConverterManager) {
        return new EsSearchEngineHandler(elasticsearchOperations, searchEngineConverterManager);
    }
}
