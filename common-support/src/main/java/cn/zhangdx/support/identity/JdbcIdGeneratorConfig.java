package cn.zhangdx.support.identity;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * JDBC 发号配置，可由调用方直接构造或绑定外部配置文件。
 * <p>生成器构造时读取配置快照，之后修改本对象不会影响已创建的生成器。</p>
 */
@Getter
@Setter
public class JdbcIdGeneratorConfig {

    private static final Pattern IDENTIFIER = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    /** 号码池表名，允许 schema.table 形式。 */
    private String tableName = "id_sequence";

    /** 号码池名称列，要求存在主键或唯一约束。 */
    private String sequenceKeyColumn = "sequence_key";

    /** 已分配的最大 ID 所在列，要求为非空有符号 BIGINT。 */
    private String currentValueColumn = "current_value";

    /** 号码池名称，必填；不同业务可使用不同的名称。 */
    private String sequenceKey;

    /** 新号码池的初始水位，首次返回 initialValue + 1；不会覆盖已有号码池。 */
    private long initialValue = 0;

    /** ID 上限，包含该值。 */
    private long maxValue = Long.MAX_VALUE;

    /** 独立发号及初始化事务的超时秒数，必须大于零。 */
    private int transactionTimeoutSeconds = 10;

    /** 是否在构造生成器时创建缺失的号码池记录；不会自动建表。 */
    private boolean initializeSequence = false;

    void validate() {
        if (tableName == null || tableName.split("\\.", -1).length > 2) {
            throw new IllegalArgumentException("ID号码池表名必须为 table 或 schema.table");
        }
        for (String part : tableName.split("\\.", -1)) {
            validateIdentifier(part);
        }
        validateIdentifier(sequenceKeyColumn);
        validateIdentifier(currentValueColumn);
        if (sequenceKeyColumn.toLowerCase(Locale.ROOT).equals(currentValueColumn.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("ID号码池名称列和水位列不能相同");
        }
        if (sequenceKey == null || sequenceKey.isBlank() || sequenceKey.length() > 64) {
            throw new IllegalArgumentException("ID号码池名称不能为空且不能超过64个字符");
        }
        if (maxValue <= 0 || initialValue < 0 || initialValue > maxValue) {
            throw new IllegalArgumentException("ID上限必须为正整数，初始水位必须在0至上限之间");
        }
        if (transactionTimeoutSeconds <= 0) {
            throw new IllegalArgumentException("ID事务超时秒数必须大于零");
        }
    }

    private static void validateIdentifier(String identifier) {
        if (identifier == null || identifier.length() > 64 || !IDENTIFIER.matcher(identifier).matches()) {
            throw new IllegalArgumentException("ID表名或列名必须以英文字母或下划线开头，仅包含英文字母、数字、下划线，且不超过64个字符");
        }
    }
}
