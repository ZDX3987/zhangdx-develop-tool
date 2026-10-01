# zhangdx-develop-tool

Java 17 工具库，包含公共 API 模型、搜索引擎封装和文件上传处理。构建基线为 Spring Boot 3.2.12；其他 Boot 版本需要在应用侧验证兼容性。

## 模块与依赖边界

| 模块 | 功能 | 调用方需要选择的依赖 |
| --- | --- | --- |
| `common-support` | 分页、响应、异常模型 | 无运行时第三方依赖 |
| `search-engine-core` | 搜索接口、Elasticsearch / Meilisearch 实现 | 对应的引擎依赖；Fastjson2 随库提供 |
| `search-engine-spring-boot3-starter` | 按类路径和配置装配搜索实现 | Elasticsearch Starter 或 Meilisearch SDK |
| `file-upload-server` | 上传接口、处理链、OSS 实现、WebP 转换 | 使用 OSS / WebP 时添加对应依赖 |
| `file-upload-spring-boot3-starter` | 按类路径和配置装配上传与图像处理 | OSS SDK / WebP 编码库，按需添加 |

两个 Starter 是通用自动配置入口，不预选具体引擎或云厂商。没有对应 SDK 时不会创建该后端的 Bean。Lombok 和注解处理器仅用于构建，不向调用方传递；日志实现由应用选择。

上传 API 当前公开使用 `MultipartFile`，因此 `spring-web` 是必需依赖。上传模块保留 `spring-core`、`spring-beans` 和 `slf4j-api` 等直接使用的 API 依赖。

## 接入

应用引入需要的 Starter，当前库版本为 `1.0.2`。下面示例假定应用已使用 Spring Boot 依赖管理；本库的父 POM 不会替应用管理显式声明的 SDK 版本。

```xml
<dependency>
    <groupId>cn.zhangdx</groupId>
    <artifactId>search-engine-spring-boot3-starter</artifactId>
    <version>1.0.2</version>
</dependency>
<!-- 使用 Meilisearch 时添加；无需另外声明 Fastjson2。 -->
<dependency>
    <groupId>com.meilisearch.sdk</groupId>
    <artifactId>meilisearch-java</artifactId>
    <version>0.20.1</version>
</dependency>
```

```yaml
zhangdx:
  search-engine:
    enabled: true
    type: meili-search
    meili:
      host: http://localhost:7700
      api-key: ${MEILI_API_KEY}
```

使用 Elasticsearch 时将 SDK 替换为 `org.springframework.boot:spring-boot-starter-data-elasticsearch`，版本由应用的 Boot BOM 管理，设置 `type: elasticsearch`，并配置 `spring.elasticsearch.uris`。自动配置在 Boot 的 Elasticsearch Data 自动配置之后执行，也支持应用自行提供 `ElasticsearchOperations`。

```xml
<dependency>
    <groupId>cn.zhangdx</groupId>
    <artifactId>file-upload-spring-boot3-starter</artifactId>
    <version>1.0.2</version>
</dependency>
<!-- 使用阿里云 OSS 时添加。 -->
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.18.3</version>
</dependency>
<!-- 开启 WebP 转换时添加。 -->
<dependency>
    <groupId>io.github.darkxanter</groupId>
    <artifactId>webp-imageio</artifactId>
    <version>0.3.3</version>
</dependency>
```

```yaml
zhangdx:
  file-upload:
    enabled: true
    vendor: aliyun
    aliyun:
      endpoint: ${OSS_ENDPOINT}
      bucket-name: ${OSS_BUCKET}
      access-key-id: ${OSS_ACCESS_KEY_ID}
      access-key-secret: ${OSS_ACCESS_KEY_SECRET}
      proxy-domain: ${OSS_PUBLIC_URL}
    rename: true
    image-config:
      only-webp: true
```

`only-webp=true` 和 WebP 编码库同时存在时才会注册转换处理器；缺少编码库时跳过该处理器，不代表已经完成格式转换。调用方应确保目标运行平台支持所选编码库的本地实现。

## 构建与验证

使用 JDK 17 或更高版本，`JAVA_HOME` 指向该 JDK。Maven Wrapper 固定 Maven 3.9.9，首次执行需要下载 Maven 和依赖。Java 编译目标固定为 17。

```sh
# 普通编译与单元测试
./mvnw verify

# 依赖收敛、单元测试、源码与 Javadoc 产物；安装到本地仓库供消费测试使用
./mvnw -Partifacts,dependency-check install

# 先执行上面的 install，再验证独立消费项目
./mvnw -N -Pconsumer-tests verify
```

Windows 使用 `mvnw.cmd`。`consumer-tests` 在 `target/it` 中构建独立项目，不继承本库父 POM，通过实际发布依赖验证自动配置发现、可选依赖隔离，以及不安装 WebP 时的普通文件处理。

测试不连接 Elasticsearch、Meilisearch 或 OSS 服务。CI 在 Java 17、21 下执行上述检查；源码/Javadoc 验证不需要签名密钥或发布凭据。

## 发布

所有模块发布普通 JAR，不执行 Spring Boot repackage。父 POM 与子模块必须一起发布。

`release` profile 包含 sources、Javadoc、GPG 签名、禁止 SNAPSHOT 的发布检查和 Central Portal 发布插件。准备好签名密钥、已验证的 `cn.zhangdx` 命名空间，并在本机 `settings.xml` 的 `central` server 中配置 Portal token 后执行：

```sh
./mvnw -Prelease,dependency-check deploy
```

发布插件设置 `autoPublish=false`：上传后在 Central Portal 检查并确认发布。普通构建和当前 CI 都不会上传。已发布版本不可重复使用；发布前更新版本、SCM tag 和固定的 `project.build.outputTimestamp`，开发期使用下一版本的 `-SNAPSHOT`。

API 二进制兼容性检查需要选定可解析的历史发布版本，本轮未设置任意基准版本。模块拆分、独立 BOM 和跨 Boot 版本的兼容矩阵列为后续改造，详见 [改造说明](docs/pom-modernization.md)。

## 许可证

[Apache License 2.0](LICENSE)
