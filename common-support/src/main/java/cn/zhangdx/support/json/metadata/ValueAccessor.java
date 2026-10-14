package cn.zhangdx.support.json.metadata;

/**
 * 属性访问接口
 * @author ZDX
 * @date 2026/10/14 17:24
 */
@FunctionalInterface
public interface ValueAccessor {

    Object get(Enum<?> value);

}
