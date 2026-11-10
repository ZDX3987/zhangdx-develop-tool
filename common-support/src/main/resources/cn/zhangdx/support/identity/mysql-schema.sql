-- 默认表结构；自定义表名或列名时，请在调用项目的迁移脚本中同步修改。
-- current_value 表示最后分配的 ID；首次分配结果为初始水位 + 1。
CREATE TABLE IF NOT EXISTS `id_sequence` (
    `sequence_key` VARCHAR(64) NOT NULL,
    `current_value` BIGINT NOT NULL,
    PRIMARY KEY (`sequence_key`)
) ENGINE=InnoDB;
