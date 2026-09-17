package cn.zhangdx.support.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

/**
 * 业务唯一ID工具
 * @author zhangdx
 * @date 2026/9/17 22:08
 */
@UtilityClass
public class BusinessIdUtil {

    /**
     * 最小业务ID，包含该值，确保没有前导零。
     */
    private static final long MIN_ID = 1_000_000_000L;

    /**
     * 业务ID上限，不包含该值，确保生成结果为10位数字。
     */
    private static final long MAX_ID_EXCLUSIVE = 10_000_000_000L;

    /**
     * 线程安全的随机数生成器，复用实例。
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成10位纯数字业务ID。
     *
     * @return 范围为1000000000至9999999999的随机业务ID
     */
    public static long nextId() {
        return RANDOM.nextLong(MIN_ID, MAX_ID_EXCLUSIVE);
    }

    /**
     * 生成字符串形式的10位纯数字业务ID。
     *
     * @return 不包含前导零的10位数字字符串
     */
    public static String nextIdStr() {
        return Long.toString(nextId());
    }
}
