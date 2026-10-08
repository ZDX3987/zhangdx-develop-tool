package cn.zhangdx.support.json;

import cn.zhangdx.support.annotation.JsonEnum;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * 自定义枚举类型序列化修改器
 * @author zhangdx
 * @date 2026/10/4 11:24
 */
public class JsonEnumSerializerModifier extends BeanSerializerModifier {

    @Override
    public JsonSerializer<?> modifyEnumSerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc,
                                                  JsonSerializer<?> serializer) {
        Class<?> rawClass = valueType.getRawClass();
        JsonEnum annotation = rawClass.getAnnotation(JsonEnum.class);
        if (annotation == null) {
            return serializer;
        }
        EnumMetadata enumMetadata = EnumMetadataCache.get(rawClass);

        return enumMetadata;
    }
}
