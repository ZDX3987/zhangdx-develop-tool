package cn.zhangdx.support.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 *
 * @author zhangdx
 * @date 2026/10/4 14:25
 */
@RequiredArgsConstructor
public class JsonEnumSerializer extends JsonSerializer<Object> {

    private final EnumMetadata enumMetadata;

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

    }
}
