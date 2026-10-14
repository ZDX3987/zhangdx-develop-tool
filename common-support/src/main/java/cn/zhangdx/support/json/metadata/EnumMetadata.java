package cn.zhangdx.support.json.metadata;

import cn.zhangdx.support.annotation.JsonEnum;
import cn.zhangdx.support.util.StringUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 枚举解析元数据类
 * @author zhangdx
 * @date 2026/10/4 10:48
 */
@Data
@RequiredArgsConstructor
public class EnumMetadata {

    private final Class<?> enumType;

    private final String identityProperty;

    private final Map<String, EnumProperty> properties;

    private final Map<Object, Enum<?>> reverseMapping;

    public static EnumMetadata create(Class<?> enumType) {
        if (!enumType.isEnum()) {
            throw new IllegalArgumentException(enumType.getName() + "is not enum");
        }
        JsonEnum annotation = enumType.getAnnotation(JsonEnum.class);
        if (annotation == null) {
            throw new IllegalArgumentException(enumType.getName() + "is not annotated with @JsonEnum");
        }
        String identity = annotation.identity();
        if (StringUtil.isEmpty(identity)) {
            throw new IllegalArgumentException("Identity must be not null");
        }
        switch (annotation.shape()) {
            case VALUE -> {

            }
            case OBJECT -> {
            }
        }
        AccessorDefinition accessorDefinition = resolveAccessor(enumType);
        Object[] enumConstants = enumType.getEnumConstants();
        Map<Object, Enum<?>> reverseMapping = new HashMap<>();
        for (Object constant : enumConstants) {
            Enum<?> enumConstant = (Enum<?>) constant;
            Object jsonValue = accessorDefinition.accessor().get(enumConstant);
            if (jsonValue == null) {
                throw new IllegalStateException("Json value cannot be null: " + enumType.getName() + "." + enumConstant.name());
            }
            reverseMapping.put(jsonValue, enumConstant);
        }
        Map<String, EnumProperty> properties = new HashMap<>();
        for (Field declaredField : enumType.getDeclaredFields()) {
            EnumProperty enumProperty = EnumProperty.build(declaredField);
            enumProperty.setAccessor(e -> {
                try {
                    return declaredField.get(e);
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Cannot read enum value field: " + declaredField, ex);
                }
            });
            properties.put(declaredField.getName(), enumProperty);
        }

        return new EnumMetadata(enumType, identity, properties, reverseMapping);
    }

    private static AccessorDefinition resolveAccessor(Class<?> enumType, Member member) {
        Class<?> valueType = null;
        ValueAccessor accessor = null;
        if (member instanceof Field field) {
            valueType = field.getType();
            accessor = e -> {
                try {
                    return field.get(e);
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Cannot read enum value field: " + field, ex);
                }
            };
        } else if (member instanceof Method method) {
            valueType = method.getReturnType();
            accessor = e -> {
                method.setAccessible(true);
                try {
                    return method.invoke(e);
                } catch (ReflectiveOperationException ex) {
                    throw new RuntimeException(ex);
                }
            };
        }
        return new AccessorDefinition(valueType, accessor);
    }

    private record AccessorDefinition(
            Class<?> valueType,
            ValueAccessor accessor
    ) { }

}
