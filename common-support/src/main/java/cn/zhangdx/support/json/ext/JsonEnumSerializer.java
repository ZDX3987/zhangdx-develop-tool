package cn.zhangdx.support.json.ext;

import cn.zhangdx.support.json.metadata.EnumMetadata;
import cn.zhangdx.support.json.metadata.EnumProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;

/**
 * 枚举自定义属性或对象序列化器
 * @author zhangdx
 * @date 2026/10/4 14:25
 */
@RequiredArgsConstructor
public class JsonEnumSerializer extends JsonSerializer<Object> {

    private final EnumMetadata enumMetadata;

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Map<String, EnumProperty> properties = enumMetadata.getProperties();
        Enum<?> enumValue = (Enum<?>) value;
        if (properties.isEmpty()) {
            EnumProperty identityProperty = enumMetadata.getIdentityProperty();
            Object propertyValue = identityProperty.getValue(enumValue);
            serializers.defaultSerializeValue(propertyValue, gen);
        } else {
            gen.writeStartObject();
            for (EnumProperty enumProperty : properties.values()) {
                gen.writeFieldName(enumProperty.getName());
                Object propertyValue = enumProperty.getValue(enumValue);
                serializers.defaultSerializeValue(propertyValue, gen);
            }
            gen.writeEndObject();
        }
    }
}
