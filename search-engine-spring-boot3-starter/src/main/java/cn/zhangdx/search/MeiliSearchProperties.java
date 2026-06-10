package cn.zhangdx.search;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MeiliSearch搜索引擎配置参数
 * @author ZDX
 * @date 2026/5/6 22:29
 */
@Data
@ConfigurationProperties(prefix = "meili-search.config")
public class MeiliSearchProperties {

    private String host;

    private String apiKey;
}
