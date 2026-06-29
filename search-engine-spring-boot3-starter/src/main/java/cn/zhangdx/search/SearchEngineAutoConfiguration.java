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
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.util.List;

/**
 * 搜索引擎自动配置类
 * @author zhangdx
 * @date 2026/6/10 19:41
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "zhangdx.search-engine", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({SearchEngineProperties.class})
public class SearchEngineAutoConfiguration {



    @Configuration
    @ConditionalOnClass(Client.class)
    @ConditionalOnProperty(prefix = "zhangdx.search-engine", name = "type", havingValue = "meili-search", matchIfMissing = true)
    static class MeiliSearchConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Client meiliSearchClient(SearchEngineProperties searchEngineProperties) {
            SearchEngineProperties.Meili meili = searchEngineProperties.getMeili();
            return new Client(new Config(meili.getHost(), meili.getApiKey()));
        }

        @Bean
        @ConditionalOnMissingBean(SearchEngineHandler.class)
        public SearchEngineHandler meiliSearchEngineHandler(Client meiliSearchClient, SearchEngineConverterManager searchEngineConverterManager) {
            return new MeiliSearchEngineHandler(meiliSearchClient, searchEngineConverterManager);
        }
    }


    @Configuration
    @ConditionalOnBean(ElasticsearchOperations.class)
    @ConditionalOnProperty(prefix = "zhangdx.search-engine", name = "type", havingValue = "elasticsearch")
    static class EsSearchConfiguration {

        @Bean
        @ConditionalOnMissingBean(SearchEngineHandler.class)
        public SearchEngineHandler esSearchEngineHandler(ElasticsearchOperations elasticsearchOperations, SearchEngineConverterManager searchEngineConverterManager) {
            return new EsSearchEngineHandler(elasticsearchOperations, searchEngineConverterManager);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SearchEngineConverterManager searchEngineConverterManager(List<SearchEngineConverter<?, ?>> searchEngineConverters) {
        SearchEngineConverterManager searchEngineConverterManager = new SearchEngineConverterManager();
        searchEngineConverterManager.setConverterList(searchEngineConverters);
        return searchEngineConverterManager;
    }
}
