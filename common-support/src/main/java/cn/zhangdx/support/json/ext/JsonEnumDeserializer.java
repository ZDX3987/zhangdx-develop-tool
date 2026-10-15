package cn.zhangdx.support.json.ext;

import cn.zhangdx.support.json.metadata.EnumMetadata;
import cn.zhangdx.support.json.metadata.EnumProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;

/**
 * 枚举反序列化
 * @author zhangdx
 * @date 2026/10/15 17:20
 */
@RequiredArgsConstructor
public class JsonEnumDeserializer extends JsonDeserializer<Object> {

    private final EnumMetadata metadata;

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
        JsonNode node = parser.getCodec().readTree(parser);
        EnumProperty identityProperty = metadata.getIdentityProperty();
        Map<String, EnumProperty> properties = metadata.getProperties();
        Object identityValue;
        if (properties.isEmpty()) {
            identityValue = context.readTreeAsValue(node, identityProperty.getType());
        } else {
            JsonNode identityNode = node.get(identityProperty.getName());
            if (identityNode == null) {
                throw JsonMappingException.from(parser, "Missing enum identity property:" + identityProperty.getName());
            }
            identityValue = context.readTreeAsValue(identityNode, identityProperty.getType());
        }

        Enum<?> enumValue = metadata.getEnumValue(identityValue);
        if (enumValue == null) {
            throw InvalidFormatException.from(parser, "Unknown enum value [" + identityValue + "]",
                    identityValue, metadata.getEnumType());
        }
        return enumValue;
    }
}
