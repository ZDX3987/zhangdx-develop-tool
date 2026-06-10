package cn.zhangdx.search.converter;

import java.util.Collection;
import java.util.List;

/**
 * 搜索模块类型转换器
 * @author zhangdx
 * @date 2026/5/9 10:09
 */
public interface SearchEngineConverter<S, T> {

    /**
     * 支持的源类型和目标类型，用于筛选转换器
     *
     * @param sourceType 源类型
     * @return 是否支持使用
     */
    boolean supports(Class<?> sourceType);

    /**
     * 对象类型转换
     * @param sourceObject 源对象
     * @return 转换后的对象
     */
    T convert(S sourceObject);

    /**
     * 批量对象类型转换
     * @param sourceObjects 源对象
     * @return 转换后的对象
     */
    default List<T> batchConvert(Collection<S> sourceObjects) {
        return sourceObjects.stream().map(this::convert).toList();
    }
}
