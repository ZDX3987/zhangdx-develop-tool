package cn.zhangdx.support.util;

/**
 * 字符串工具类
 * @author zhangdx
 * @date 2026/10/14 23:07
 */
public final class StringUtil {

    private StringUtil() {}

    /**
     * 判断字符串是否为空，但不包含空白字符
     * @param str 要判断的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
