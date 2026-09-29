# POM 改造说明

本轮保留 5 个模块及其公开包名，便于逐项评审。没有更改 Java 17 和 Spring Boot 3.2.12 基线，也没有执行远程发布。

## 可分别评审的改动

| 对应建议 | 本轮改动 | 取舍及影响 |
| --- | --- | --- |
| 5、8：依赖边界 | OSS / WebP 设为 optional，日志和 Spring API 显式声明；Fastjson2 正常传递 | 上传调用方需要补充所用 SDK；这是接入方式变化 |
| 6：核心依赖 | 搜索核心改用 Spring Data Elasticsearch / Commons，移除 Boot Starter | 继续保留原实现类，未拆模块 |
| 7：Starter 语义 | 必需 Boot API 正常传递，后端 SDK 可选；README 列出接入组合 | 保留通用自动配置入口，没有新增厂商 Starter |
| 9：JSON / 测试 | Fastjson1 改为 Fastjson2，JUnit3 模板测试改为 Jupiter 的有效测试 | 覆盖重复引用、中文、查询结果映射；应用自定义序列化行为仍需结合自身模型验证 |
| 10：构建 | 固定插件、Java release、Maven/JDK 检查、依赖收敛 profile | 依赖收敛在 CI 和显式 profile 中执行，普通构建不强制启用 |
| 11：处理器 | 显式处理器路径、Lombok 在配置元数据处理器之前、自动配置元数据处理器 | 注解处理器不作为运行时依赖传递 |
| 12：发布 | Apache-2.0、开发者/SCM/模块说明、sources/Javadoc/GPG/Central profile | 本地验证无需密钥；签名、命名空间及真实发布尚需发布环境验证 |
| 13：质量 | Maven Wrapper、固定产物时间、Java 17/21 CI、独立消费测试 | 未假定不存在的历史版本作为 API 兼容基准 |

当前检出的父 POM 尚未导入 Boot BOM，两个 Starter 也未声明自身使用的 Lombok。本轮补齐这些构建前提；已有内部模块版本修正保留。内部模块仍由父 POM 集中管理为 1.0.2。

## 相关源码变化

- Meilisearch 处理器替换 JSON API；Fastjson2 默认关闭引用检测，与原来的 `DisableCircularReferenceDetect` 设置一致。
- Elasticsearch 自动配置增加类路径条件，并声明在 Boot Elasticsearch Data 自动配置之后执行。
- WebP 自动配置放入带类路径条件的嵌套配置，仅在编码库和开关同时存在时启用。
- `FileUtil` 中直接引用 WebP 编码器的代码隔离到延迟加载的内部类，避免普通重命名也要求安装 WebP；独立消费测试覆盖此行为。
- 上传处理链允许调用方提供自己的 Bean。

现有功能启用默认值保持原行为：搜索需要显式配置 `enabled=true`；上传未配置开关时仍尝试自动配置。没有借本轮 POM 改造统一这两个默认值。

## 后续单独评审

1. 将引擎/厂商实现拆分到独立适配模块，并提供按后端组合的 Starter。需要同时规划已发布 artifact 的兼容迁移。
2. 若使用方经常组合多个模块，再提供只管理对外版本的独立 BOM。
3. 选定真实历史发布坐标后，再配置 japicmp 或 Revapi 的二进制兼容性门禁。
4. 针对更多 Boot 3.x 版本和 WebP 本地编码平台补充兼容测试。

## 官方参考

- [Maven 依赖机制](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)
- [Spring Boot 自定义自动配置](https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html)
- [Fastjson2 迁移说明](https://github.com/alibaba/fastjson2/wiki/fastjson_1_upgrade_cn)
- [Central 发布要求](https://central.sonatype.org/publish/requirements/)
- [Central Maven 发布插件](https://central.sonatype.org/publish/publish-portal-maven/)
- [Maven 可重复构建](https://maven.apache.org/guides/mini/guide-reproducible-builds.html)
