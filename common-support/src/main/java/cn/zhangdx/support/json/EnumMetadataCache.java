package cn.zhangdx.support.json;

import cn.zhangdx.support.json.metadata.EnumMetadata;

/**
 *
 * @author zhangdx
 * @date 2026/10/4 10:58
 */
public final class EnumMetadataCache {

    private EnumMetadataCache() {}

    private static final ClassValue<EnumMetadata> CACHE = new ClassValue<>() {
        @Override
        protected EnumMetadata computeValue(Class<?> type) {
            return EnumMetadata.create(type);
        }
    };

    static EnumMetadata get(Class<?> enumType) {
        return CACHE.get(enumType);
    }
}
