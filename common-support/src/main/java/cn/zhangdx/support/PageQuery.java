package cn.zhangdx.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *
 * @author zhangdx
 * @date 2026/6/10 19:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PageQuery<T> {

    private long pageSize;

    private long current;

    public PageQuery(PageQuery<?> otherPageQuery) {
        this.current = otherPageQuery.getCurrent();
        this.pageSize = otherPageQuery.getPageSize();
    }

    public ResultPage<T> buildPage() {
        ResultPage<T> page = new ResultPage<>();
        page.setCurrent(current);
        page.setPageSize(pageSize);
        return page;
    }

    public <E> ResultPage<E> buildOtherTypePage(Class<E> otherType) {
        ResultPage<E> page = new ResultPage<>();
        page.setCurrent(current);
        page.setPageSize(pageSize);
        return page;
    }
}
