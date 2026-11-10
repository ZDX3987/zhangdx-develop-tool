package cn.zhangdx.support.identity;

import cn.zhangdx.support.exception.IdGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * 基于数据库行锁的递增ID生成器，适用于MySQL
 * <p>同一号码池的所有调用由数据库串行分配，线程间和应用实例间均不重复。
 * 发号使用独立事务，外层业务回滚不会回收已分配号码。</p>
 * <p>表必须使用支持事务和行锁的存储引擎（MySQL InnoDB），号码池名称列必须有唯一约束。
 * 使用该类需要单独依赖 {@code spring-tx}、{@code spring-jdbc} 和 {@code slf4j-api}。
 * 表结构由调用方管理，生成器不会自动建表。</p>
 * @author zhangdx
 * @date 2026/10/29 21:47
 */
@Slf4j
public class JdbcIdGenerator implements IdGenerator {

    private final String selectCurrentValueSql;

    private final String updateCurrentValueSql;

    private final String initializeSequenceSql;

    private final JdbcTemplate jdbcTemplate;

    private final TransactionTemplate allocationTransaction;

    private final String sequenceKey;

    private final long maxValue;

    private final long initialValue;

    private static final String SELECT_SQL_CHUNK = "SELECT %s FROM %s WHERE %s = ? FOR UPDATE";
    private static final String UPDATE_SQL_CHUNK = "UPDATE %s SET %s = ? WHERE %s = ?";
    private static final String INSERT_SQL_CHUNK = "SELECT %s FROM %s WHERE %s = ? FOR UPDATE";

    /**
     * 使用默认表结构，不自动初始化号码池，保留原有接入方式。
     * @param dataSource 发号数据源
     * @param sequenceKey 号码池名称
     * @param maxValue ID 上限（包含）
     */
    public JdbcIdGenerator(DataSource dataSource, String sequenceKey, long maxValue) {
        this(dataSource, defaultConfig(sequenceKey, maxValue));
    }

    /**
     * 按配置创建生成器；开启 initializeSequence 时，在独立事务中初始化缺失的号码池。
     * @param dataSource 发号数据源（连接配置及生命周期由调用方管理）
     * @param config 发号配置
     */
    public JdbcIdGenerator(DataSource dataSource, JdbcIdGeneratorConfig config) {
        if (dataSource == null || config == null) {
            throw new IllegalArgumentException("ID发号数据源和配置不能为空");
        }
        config.validate();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.sequenceKey = config.getSequenceKey();
        this.maxValue = config.getMaxValue();
        this.initialValue = config.getInitialValue();
        String table = quoteIdentifier(config.getTableName());
        String keyColumn = quoteIdentifier(config.getSequenceKeyColumn());
        String valueColumn = quoteIdentifier(config.getCurrentValueColumn());
        this.selectCurrentValueSql = SELECT_SQL_CHUNK.formatted(valueColumn, table, keyColumn);
        this.updateCurrentValueSql = UPDATE_SQL_CHUNK.formatted(table, valueColumn, keyColumn);
        this.initializeSequenceSql = "INSERT INTO " + table + " (" + keyColumn + ", " + valueColumn
                + ") VALUES (?, ?) ON DUPLICATE KEY UPDATE " + keyColumn + " = " + keyColumn;
        this.allocationTransaction = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        this.allocationTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.allocationTransaction.setTimeout(config.getTransactionTimeoutSeconds());
        if (config.isInitializeSequence()) {
            initializeSequence();
        }
        log.info("初始化数据库ID生成器，sequenceKey={}，maxValue={}", sequenceKey, maxValue);
    }

    /**
     * 在独立事务中创建缺失的号码池。可重复调用及多实例同时调用，已有水位不会被重置。
     * <p>仅插入号码池记录，调用前必须已完成建表。</p>
     */
    public void initializeSequence() {
        try {
            allocationTransaction.executeWithoutResult(status ->
                    jdbcTemplate.update(initializeSequenceSql, sequenceKey, initialValue));
        } catch (DataAccessException | TransactionException ex) {
            throw new IdGenerationException("ID号码池初始化失败，请检查表或数据库状态：" + sequenceKey, ex);
        }
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
        } catch (EmptyResultDataAccessException ex) {
            throw new IdGenerationException("ID号码池未初始化：" + sequenceKey, ex);
        } catch (DataAccessException | TransactionException ex) {
            log.error("ID分配失败，sequenceKey={}", sequenceKey, ex);
            throw new IdGenerationException("ID分配失败，请检查表或数据库状态", ex);
        }
    }

    private long allocateNextId() {
        long currentValue = jdbcTemplate.queryForObject(selectCurrentValueSql, Long.class, sequenceKey);
        if (currentValue < 0) {
            throw new IdGenerationException("ID号码池水位无效：" + sequenceKey);
        }
        if (currentValue >= maxValue) {
            log.error("ID分配达到上限，sequenceKey={}，currentValue={}，maxValue={}", sequenceKey, currentValue, maxValue);
            throw new IdGenerationException("ID分配已达到上限：" + sequenceKey + ", maxValue: " + maxValue);
        }
        long nextId = currentValue + 1;
        if (jdbcTemplate.update(updateCurrentValueSql, nextId, sequenceKey) != 1) {
            throw new IdGenerationException("ID号码池更新失败：" + sequenceKey);
        }
        log.debug("公共ID分配完成，sequenceKey={}, allocatedId={}", sequenceKey, nextId);
        return nextId;
    }

    private static JdbcIdGeneratorConfig defaultConfig(String sequenceKey, long maxValue) {
        JdbcIdGeneratorConfig config = new JdbcIdGeneratorConfig();
        config.setSequenceKey(sequenceKey);
        config.setMaxValue(maxValue);
        return config;
    }

    private static String quoteIdentifier(String identifier) {
        // 标识符已由配置校验，值始终使用 JDBC 参数绑定。
        return "`" + identifier.replace(".", "`.`") + "`";
    }
}
