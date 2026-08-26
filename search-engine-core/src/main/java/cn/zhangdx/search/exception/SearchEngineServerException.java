package cn.zhangdx.search.exception;

/**
 * 搜索引擎服务端异常
 * @author ZDX
 * @date 2026/8/26 18:13
 */
public class SearchEngineServerException extends RuntimeException {

    public SearchEngineServerException(String message) {
        super(message);
    }
}
