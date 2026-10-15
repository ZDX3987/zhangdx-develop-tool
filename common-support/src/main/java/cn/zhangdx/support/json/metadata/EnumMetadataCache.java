package cn.zhangdx.support.json.metadata;

/**
 * 枚举转JSON元数据缓存
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

    public static EnumMetadata get(Class<?> enumType) {
        return CACHE.get(enumType);
    }
}
