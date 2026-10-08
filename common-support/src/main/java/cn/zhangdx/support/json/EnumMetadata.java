package cn.zhangdx.support.json;

import cn.zhangdx.support.annotation.JsonEnum;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 枚举解析元数据类
 * @author zhangdx
 * @date 2026/10/4 10:48
 */
@RequiredArgsConstructor
public class EnumMetadata {

    private final Class<?> enumType;

    private final Class<?> valueType;

    private final ValueAccessor valueAccessor;

    private final Map<Object, Enum<?>> reverseMapping;

    public static EnumMetadata create(Class<?> enumType) {
        if (!enumType.isEnum()) {
            throw new IllegalArgumentException(enumType.getName() + "is not enum");
        }
        JsonEnum annotation = enumType.getAnnotation(JsonEnum.class);
        if (annotation == null) {
            throw new IllegalArgumentException(enumType.getName() + "is not annotated with @JsonEnum");
        }

        return new EnumMetadata();
    }

    private static AccessorDefinition resolveAccessor(Class<?> enumType) {

    }

    @FunctionalInterface
    private interface ValueAccessor {

        Object get(Enum<?> value);
    }

    private record AccessorDefinition(
            Class<?> valueType,
            ValueAccessor accessor
    ) { }

}
