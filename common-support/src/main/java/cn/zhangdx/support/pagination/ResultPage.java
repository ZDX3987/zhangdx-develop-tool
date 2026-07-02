package cn.zhangdx.support.pagination;

import lombok.Data;

import java.util.List;

/**
 * 结果分页类
 * @author zhangdx
 * @date 2026/6/10 19:23
 */
@Data
public class ResultPage<E> {

    private long total;

    private long pageSize;

    private long current;

    private List<E> records;

    public ResultPage() {}

    public ResultPage(long current, long pageSize) {
        this.current = current;
        this.pageSize = pageSize;
    }
}
