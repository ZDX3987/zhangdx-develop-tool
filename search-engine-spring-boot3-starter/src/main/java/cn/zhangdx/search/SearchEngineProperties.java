package cn.zhangdx.search;

import cn.zhangdx.search.enumration.EngineType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 搜索引擎 starter 配置参数。
 *
 * @author zhangdx
 * @date 2026/6/29
 */
@Data
@ConfigurationProperties(prefix = "zhangdx.search-engine")
public class SearchEngineProperties {

    /**
     * 是否启用搜索引擎自动配置。
     */
    private boolean enabled = true;

    /**
     * 当前使用的搜索引擎类型。
     */
    private EngineType type = EngineType.MEILI_SEARCH;

    /**
     * MeiliSearch 配置。
     */
    private Meili meili = new Meili();

    @Data
    public static class Meili {

        /**
         * MeiliSearch 服务地址，例如 http://localhost:7700。
         */
        private String host;

        /**
         * MeiliSearch API Key。
         */
        private String apiKey;
    }
}
