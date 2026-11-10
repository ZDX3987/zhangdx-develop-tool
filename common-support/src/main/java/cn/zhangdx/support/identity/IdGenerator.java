package cn.zhangdx.support.identity;

/**
 * 公共数字ID生成服务
 * @author zhangdx
 * @date 2026/10/29 21:46
 */
public interface IdGenerator {

    /**
     * 分配一个正整数ID
     * @return 已分配的ID
     */
    long nextId();

    /**
     * 分配一个ID，返回字符串类型
     * @return 不包含前导零的数字字符串
     */
    default String nextIdStr() {
        return Long.toString(nextId());
    }
}
