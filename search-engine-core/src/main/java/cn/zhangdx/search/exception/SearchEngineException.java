package cn.zhangdx.search.exception;

import cn.zhangdx.search.enumration.EngineType;

/**
 * 搜索引擎操作异常
 * @author zhangdx
 * @date 2026/9/16 22:32
 */
public class SearchEngineException extends RuntimeException {

    private final EngineType throwEngineType;

    public SearchEngineException(EngineType throwEngineType, String message) {
        super(message);
        this.throwEngineType = throwEngineType;
    }

    public SearchEngineException(EngineType throwEngineType, String message, Throwable cause) {
        super(message, cause);
        this.throwEngineType = throwEngineType;
    }

    public SearchEngineException(EngineType throwEngineType, Throwable cause) {
        super(cause);
        this.throwEngineType = throwEngineType;
    }
}
