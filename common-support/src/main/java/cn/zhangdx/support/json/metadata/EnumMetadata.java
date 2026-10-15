package cn.zhangdx.support.json.metadata;

import cn.zhangdx.support.json.annotation.JsonEnum;
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

    private final EnumProperty identityProperty;

    private final Map<String, EnumProperty> properties;

    private final Map<Object, Enum<?>> reverseMapping;

    public Enum<?> getEnumValue(Object identityValue) {
        return reverseMapping.get(identityValue);
    }

    public static EnumMetadata create(Class<?> enumType) {
        if (!enumType.isEnum()) {
            throw new IllegalArgumentException(enumType.getName() + "is not enum");
        }
        JsonEnum annotation = enumType.getAnnotation(JsonEnum.class);
        if (annotation == null) {
            throw new IllegalArgumentException(enumType.getName() + "is not annotated with @JsonEnum");
        }
        Map<String, EnumProperty> properties = new HashMap<>();
        String identity = annotation.identity();
        if (StringUtil.isEmpty(identity)) {
            throw new IllegalArgumentException("Identity must be not null");
        }
        EnumProperty identityProperty = null;
        boolean isShapeValue = JsonEnum.Shape.VALUE.equals(annotation.shape());
        for (Field declaredField : enumType.getDeclaredFields()) {
            if (enumType.equals(declaredField.getType()) || enumType.equals(declaredField.getType().componentType())) {
                continue;
            }
            if (identityProperty == null && identity.equals(declaredField.getName())) {
                identityProperty = buildProperties(declaredField);
                if (isShapeValue) {
                    break;
                }
                properties.put(declaredField.getName(), buildProperties(declaredField));
            } else if (!isShapeValue) {
                properties.put(declaredField.getName(), buildProperties(declaredField));
            }
        }
        Map<Object, Enum<?>> reverseMapping = new HashMap<>();
        for (Object constant : enumType.getEnumConstants()) {
            Enum<?> enumConstant = (Enum<?>) constant;
            Object jsonValue = identityProperty == null ? enumConstant.name()
                    : identityProperty.getAccessor().get(enumConstant);
            if (jsonValue == null) {
                throw new IllegalStateException("Json value cannot be null: " + enumType.getName() + "." + enumConstant.name());
            }
            reverseMapping.put(jsonValue, enumConstant);
        }

        return new EnumMetadata(enumType, identityProperty, properties, reverseMapping);
    }

    private static EnumProperty buildProperties(Field declaredField) {
        EnumProperty enumProperty = EnumProperty.build(declaredField);
        AccessorDefinition fieldAccessorDefinition = buildAccessorDefinition(declaredField);
        enumProperty.setAccessor(fieldAccessorDefinition.accessor());
        return enumProperty;
    }

    private static AccessorDefinition buildAccessorDefinition(Member member) {
        Class<?> valueType = null;
        ValueAccessor accessor = null;
        if (member instanceof Field field) {
            valueType = field.getType();
            accessor = e -> {
                field.setAccessible(true);
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
                    throw new IllegalStateException("Cannot invoke enum method: " + method.getName(), ex);
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
