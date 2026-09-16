package cn.zhangdx.support.util;

import lombok.experimental.UtilityClass;

/**
 * 通用字符串工具类
 * @author zhangdx
 * @date 2026/9/16 22:22
 */
@UtilityClass
public class StringUtil {

    /**
     * 判断是否是空字符串
     * @param content 字符串参数
     * @return 是否是空字符串
     */
    public static boolean isEmpty(String content) {
        return content == null || content.isEmpty();
    }

    /**
     * 判断是否是空白字符串
     * @param content 字符串参数
     * @return 是否是空白字符串
     */
    public static boolean isBlank(String content) {
        return content == null || content.isBlank();
    }
}
