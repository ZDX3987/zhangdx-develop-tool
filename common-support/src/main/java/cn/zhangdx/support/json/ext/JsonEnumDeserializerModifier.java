package cn.zhangdx.support.json.ext;

import cn.zhangdx.support.json.annotation.JsonEnum;
import cn.zhangdx.support.json.metadata.EnumMetadata;
import cn.zhangdx.support.json.metadata.EnumMetadataCache;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

/**
 * 枚举反序列化修改器
 * @author zhangdx
 * @date 2026/10/15 18:25
 */
public class JsonEnumDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc,
                                                      JsonDeserializer<?> deserializer) {
        Class<?> enumType = type.getRawClass();
        JsonEnum annotation = enumType.getAnnotation(JsonEnum.class);
        if (annotation == null) {
            return deserializer;
        }
        EnumMetadata enumMetadata = EnumMetadataCache.get(enumType);
        return new JsonEnumDeserializer(enumMetadata);
    }
}
