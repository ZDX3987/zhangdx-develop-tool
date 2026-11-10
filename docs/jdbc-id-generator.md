# JDBC 业务 ID 生成器

`common-support` 的 `cn.zhangdx.support.identity` 包提供 `IdGenerator` 接口及 MySQL JDBC 实现。每次发号通过数据库行锁递增水位，并使用独立事务提交。同一张表、同一个号码池名称在多个线程和应用实例间共享进度；业务事务回滚不会回收已分配的号码，允许出现断号。

## 依赖与数据库准备

当前开发版本为 `common-support:1.0.3-SNAPSHOT`。使用方自行提供数据源、JDBC 驱动、Spring JDBC / 事务依赖和日志 API；库中这些依赖保持 optional，不向只使用其他公共工具的项目传递。

Spring Boot 3 项目示例（Spring 和驱动版本由应用的 Boot BOM 管理）：

```xml
<dependency>
    <groupId>cn.zhangdx</groupId>
    <artifactId>common-support</artifactId>
    <version>1.0.3-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

普通 Java 项目也可直接使用；请显式引入 `spring-jdbc`、`spring-tx`、`slf4j-api`、数据源实现及 MySQL 驱动，自行管理其兼容版本。

先在调用项目的 Flyway / Liquibase 或其他迁移流程中创建表。默认建表脚本随 JAR 提供，位置为 `cn/zhangdx/support/identity/mysql-schema.sql`，内容如下：

```sql
CREATE TABLE IF NOT EXISTS `id_sequence` (
    `sequence_key` VARCHAR(64) NOT NULL,
    `current_value` BIGINT NOT NULL,
    PRIMARY KEY (`sequence_key`)
) ENGINE=InnoDB;
```

自定义表名、列名时需同步修改迁移脚本。号码池名称必须有唯一约束，水位必须使用非空、有符号 `BIGINT`，表必须使用支持事务和行锁的 InnoDB 引擎。生成器不会执行 DDL。

## 配置项

`JdbcIdGeneratorConfig` 是普通 JavaBean，可直接赋值，也可由调用项目绑定 YAML / properties。生成器在构造时读取配置快照，后续修改配置对象不会影响现有实例。

| 属性 | 默认值 | 含义 |
| --- | --- | --- |
| `sequenceKey` | 无，必填 | 号码池名称，非空且不超过 64 个字符 |
| `tableName` | `id_sequence` | 表名，支持 `schema.table` |
| `sequenceKeyColumn` | `sequence_key` | 号码池名称列 |
| `currentValueColumn` | `current_value` | 最后分配的 ID 所在列 |
| `initialValue` | `0` | 新号码池的初始水位，首次发号为该值加一 |
| `maxValue` | `Long.MAX_VALUE` | ID 上限，包含该值 |
| `transactionTimeoutSeconds` | `10` | 发号和初始化事务的超时秒数，必须大于零 |
| `initializeSequence` | `false` | 构造时自动创建缺失的号码池记录，不创建表、不修改已有水位 |

初始水位必须大于等于零且不超过上限，上限必须为正数；水位等于上限时号码池已耗尽。表名各段及列名只能包含英文字母、数字、下划线，不能以数字开头，且每段最多 64 个字符；列名不能相同。SQL 标识符经过校验及反引号引用，号码池名称和水位通过参数绑定。

## Spring Boot 3 接入

本版使用方显式注册配置与生成器 Bean，无需扫描工具库包。以下代码放在调用项目中，由应用正常扫描：

```java
import cn.zhangdx.support.identity.IdGenerator;
import cn.zhangdx.support.identity.JdbcIdGenerator;
import cn.zhangdx.support.identity.JdbcIdGeneratorConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
public class BusinessIdConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "zhangdx.identity.jdbc")
    public JdbcIdGeneratorConfig businessIdConfig() {
        return new JdbcIdGeneratorConfig();
    }

    @Bean
    @DependsOnDatabaseInitialization
    public IdGenerator businessIdGenerator(DataSource dataSource,
                                           JdbcIdGeneratorConfig businessIdConfig) {
        return new JdbcIdGenerator(dataSource, businessIdConfig);
    }
}
```

```yaml
zhangdx:
  identity:
    jdbc:
      sequence-key: ${ID_SEQUENCE_KEY:order}
      table-name: id_sequence
      sequence-key-column: sequence_key
      current-value-column: current_value
      # 首次生成 1000000000，上限为 9999999999，形成 10 位数字 ID。
      initial-value: 999999999
      max-value: 9999999999
      transaction-timeout-seconds: 10
      initialize-sequence: true
```

业务类通过构造器注入 `IdGenerator`，调用 `nextId()` 或 `nextIdStr()`。存在多个数据源时，在 Bean 参数上通过 `@Qualifier` 选择发号数据源。多个业务号码池可各自绑定不同的配置前缀并创建不同名称的生成器 Bean，注入时也使用 `@Qualifier` 区分。

示例通过 `@DependsOnDatabaseInitialization` 等待 Spring Boot 管理的数据库初始化（包括 Flyway / Liquibase）完成；如果项目有自定义的延迟迁移流程，应确保建表完成后再创建开启 `initializeSequence` 的生成器。

## 直接构造

```java
JdbcIdGeneratorConfig config = new JdbcIdGeneratorConfig();
config.setSequenceKey("order");
config.setInitialValue(999_999_999L);
config.setMaxValue(9_999_999_999L);
config.setInitializeSequence(true);
IdGenerator generator = new JdbcIdGenerator(dataSource, config);
long id = generator.nextId();
String nextId = generator.nextIdStr();
```

原构造方法 `new JdbcIdGenerator(dataSource, sequenceKey, maxValue)` 继续可用，使用默认表名及列名，不自动初始化。也可在建表完成后显式调用 `JdbcIdGenerator.initializeSequence()` 创建缺失的记录。

关闭自动初始化时，由迁移脚本插入号码池记录，例如：

```sql
INSERT INTO `id_sequence` (`sequence_key`, `current_value`)
VALUES ('order', 999999999);
```

## 初始化及运行约定

- 初始化可重复执行，并支持多个实例同时初始化同一个号码池；已存在的记录不会回退或抬高水位。修改 `initialValue` 仅影响之后新建的号码池，不能通过改配置重置现有记录。
- 同一号码池的各实例应使用一致的表、列、初始水位和上限配置。迁移已有业务时，初始水位须覆盖历史已使用的 ID；运行后不要删除号码池或回退数据库水位。
- 表缺失、号码池未初始化、水位为空或负数、号码耗尽及数据库异常均以 `IdGenerationException` 报告，数据库异常保留原始原因。
- 每次调用执行一次独立事务。同一号码池的发号会在数据库串行执行；在外层业务事务内发号需要额外数据库连接，连接池需预留相应容量。
- 这版 SQL 面向 MySQL。测试使用 H2 的 MySQL 兼容模式覆盖初始化、并发和事务语义，实际部署前仍需在目标 MySQL 环境验证。

`cn.zhangdx.support.util.BusinessIdUtil` 是独立的随机 ID 工具，不维护数据库水位；需要跨实例递增发号时应使用本页的 `IdGenerator`。
