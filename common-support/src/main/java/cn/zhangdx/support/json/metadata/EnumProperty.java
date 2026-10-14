package cn.zhangdx.support.json.metadata;

import lombok.Data;

import java.lang.reflect.Field;

/**
 * 枚举属性表示类
 * @author ZDX
 * @date 2026/10/14 17:23
 */
@Data
public class EnumProperty {

    private String name;

    private Class<?> type;

    private ValueAccessor accessor;

    public Object getValue(Enum<?> value) {
        return this.accessor.get(value);
    }

    public static EnumProperty build(Field field) {
        EnumProperty enumProperty = new EnumProperty();
        enumProperty.setName(field.getName());
        enumProperty.setType(field.getType());
        return enumProperty;
    }
}
