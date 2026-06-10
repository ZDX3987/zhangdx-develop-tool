package cn.zhangdx.search;

import cn.zhangdx.search.converter.SearchEngineConverter;
import cn.zhangdx.search.converter.SearchEngineConverterManager;
import cn.zhangdx.search.handler.EsSearchEngineHandler;
import cn.zhangdx.search.handler.MeiliSearchEngineHandler;
import cn.zhangdx.search.handler.SearchEngineHandler;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.util.List;

/**
 *
 * @author zhangdx
 * @date 2026/6/10 19:41
 */
@Configuration
@EnableConfigurationProperties(MeiliSearchProperties.class)
public class SearchEngineAutoConfiguration {

    @Bean
    public Client meiliSearchClient(MeiliSearchProperties meiliSearchProperties) {
        return new Client(new Config(meiliSearchProperties.getHost(), meiliSearchProperties.getApiKey()));
    }

    @Bean
    public SearchEngineConverterManager searchEngineConverterManager(List<SearchEngineConverter<?, ?>> searchEngineConverters) {
        SearchEngineConverterManager searchEngineConverterManager = new SearchEngineConverterManager();
        searchEngineConverterManager.setConverterList(searchEngineConverters);
        return searchEngineConverterManager;
    }

    @Bean
    @ConditionalOnBean(Client.class)
    public SearchEngineHandler meiliSearchEngineHandler(Client meiliSearchClient, SearchEngineConverterManager searchEngineConverterManager) {
        return new MeiliSearchEngineHandler(meiliSearchClient, searchEngineConverterManager);
    }

    @Bean
    @ConditionalOnProperty(value = "spring.datasource.elasticsearch.enabled", havingValue = "true")
    public SearchEngineHandler esSearchEngineHandler(ElasticsearchOperations elasticsearchOperations, SearchEngineConverterManager searchEngineConverterManager) {
        return new EsSearchEngineHandler(elasticsearchOperations, searchEngineConverterManager);
    }
}
