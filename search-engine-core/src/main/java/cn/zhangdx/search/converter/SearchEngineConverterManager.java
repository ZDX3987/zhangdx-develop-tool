package cn.zhangdx.search.converter;

import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author zhangdx
 * @date 2026/5/9 10:21
 */
@Data
public class SearchEngineConverterManager {

    private List<SearchEngineConverter<?, ?>> converterList;

    @SuppressWarnings("unchecked")
    public <S, T> T convert(S sourceObject) {
        for (SearchEngineConverter<?, ?> converter : converterList) {
            if (converter.supports(sourceObject.getClass())) {
                return ((SearchEngineConverter<S, T>) converter).convert(sourceObject);
            }
        }
        throw new UnsupportedOperationException("no support converter for " + sourceObject.getClass());
    }

    @SuppressWarnings("unchecked")
    public <S, T> List<T> batchConvert(Collection<S> sourceObject) {
        Object[] sourceObjectArray = sourceObject.toArray();
        Class<?> sourceObjectType = sourceObjectArray[0].getClass();
        for (SearchEngineConverter<?, ?> converter : converterList) {
            if (converter.supports(sourceObjectType)) {
                return ((SearchEngineConverter<S, T>) converter).batchConvert(sourceObject);
            }
        }
        throw new UnsupportedOperationException("no support converter for " + sourceObject.getClass());
    }
}
