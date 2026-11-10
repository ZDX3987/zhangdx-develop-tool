package cn.zhangdx.support.identity;

import cn.zhangdx.support.exception.IdGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * 基于数据库行锁的递增ID生成器，适用于MySQL
 * <p>同一号码池的所有调用由数据库串行分配，线程间和应用实例间均不重复。
 * 发号使用独立事务，外层业务回滚不会回收已分配号码。</p>
 * 使用该类需要单独依赖<code>spring-tx spring-jdbc<code/>
 * @author zhangdx
 * @date 2026/10/29 21:47
 */
@Slf4j
public class JdbcIdGenerator implements IdGenerator {

    private static final String SELECT_CURRENT_VALUE =
            "";

    private static final String UPDATE_CURRENT_VALUE =
            "";

    private final JdbcTemplate jdbcTemplate;

    private final TransactionTemplate allocationTransaction;

    private final String sequenceKey;

    private final long maxValue;

    public JdbcIdGenerator(DataSource dataSource, String sequenceKey, long maxValue) {
        Assert.notNull(dataSource, "ID发号数据源不能为空");
        Assert.hasText(sequenceKey, "ID号码池名称不能为空");
        Assert.isTrue(sequenceKey.length() <= 64, "ID号码池名称不能超过64个字符");
        Assert.isTrue(maxValue > 0, "ID上限必须为正整数");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.sequenceKey = sequenceKey;
        this.maxValue = maxValue;
        this.allocationTransaction = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        this.allocationTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.allocationTransaction.setTimeout(10);
        log.info("初始化数据库ID生成器，sequenceKey={}，maxValue={}", sequenceKey, maxValue);
    }

    /**
     * 分配一个正整数ID
     *
     * @return 已分配的ID
     */
    @Override
    public long nextId() {
        log.debug("申请公共ID，sequenceKey={}", sequenceKey);
        try {
            Long allocatedId = allocationTransaction.execute(status -> allocateNextId());
            return Objects.requireNonNull(allocatedId, "ID发号事务未返回结果");
        } catch (DataAccessException | TransactionException ex) {
            log.error("ID分配失败，sequenceKey={}", sequenceKey, ex);
            throw new IdGenerationException("ID分配失败，请检查表或数据库状态", ex);
        }
    }

    private long allocateNextId() {
        long currentValue;
        currentValue = jdbcTemplate.queryForObject(SELECT_CURRENT_VALUE, Long.class, sequenceKey);
        if (currentValue < 0) {
            throw new IdGenerationException("ID号码池水位无效：" + sequenceKey);
        }
        if (currentValue >= maxValue) {
            log.error("ID分配达到上限，sequenceKey={}，currentValue={}，maxValue={}", sequenceKey, currentValue, maxValue);
            throw new IdGenerationException("ID分配以达到上限：" + sequenceKey + ", maxValue: " + maxValue);
        }
        long nextId = currentValue + 1;
        if (jdbcTemplate.update(UPDATE_CURRENT_VALUE, nextId, sequenceKey) != 1) {
            throw new IdGenerationException("ID号码池更新失败：" + sequenceKey);
        }
        log.debug("公共ID分配完成，sequenceKey={}, allocatedId={}", sequenceKey, nextId);
        return nextId;
    }
}
