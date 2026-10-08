package cn.zhangdx.support.json;

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
            return null;
        }
    };

    static EnumMetadata get(Class<?> enumType) {
        return CACHE.get(enumType);
    }
}
