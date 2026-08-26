package cn.zhangdx.search.exception;

/**
 * 搜索引擎请求参数异常
 * @author ZDX
 * @date 2026/8/26 17:44
 */
public class SearchEngineRequestException extends RuntimeException {

    public SearchEngineRequestException(String message) {
        super(message);
    }

    public SearchEngineRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
