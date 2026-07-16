# zhangdx-develop-tool

## 概述
这是一个个人用的Java Maven的多module的依赖库，提供了一些个人常用的依赖功能实现：

1. 基于Elasticsearch和Meilisearch封装的search engine功能实现。
2. 实现了一些其他项目可复用的内容：自定义Java异常、统一API返回结果类、查询列表分页类等
3. 实现了一些个人开发的常用工具类（util）

## 技术栈说明
* 项目结构基于Maven构建
* 开发语言是Java 17

## 项目结构说明
外层是一个pom类型的结构，内层包含以下module：
### common-support
存放公共类的项目，包含其他module用到的类和其他项目可以依赖的独立工具类。外层包名是`cn.zhangdx.support`
* exception包：自定义的异常类，包含抽象类和具体实现类。
* pagination包：自定义的分页接收类。
* rest包：自定义的http接口返回内容包装类，以及内部状态码。

### search-engine-core
搜索引擎功能的核心实现项目，基于Elasticsearch和Meilisearch，可基于配置选择其一使用搜索功能。定义了基于注解的存储声明以及Handler客户端，
还支持可扩展的搜索内容converter。外层包名是`cn.zhangdx.search`.
* annotation：定义的注解，用来支持存储数据和搜索结果实体类上的定义
* converter：支持搜索返回结果的映射转换等扩展，在搜索之后执行。
* handler：核心执行搜索的类，目前有两种具体实现。
* query、utils

### search-engine-spring-boot3-starter
对于search-engine-core的基于Spring Boot3的Starter，外部项目主要通过该Starter来使用search-engine。
* 包含一个自定义配置映射类`SearchEngineProperties`和自动装配类`SearchEngineAutoConfiguration`。
* 自动导入文件：META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports

## 开发注意事项
* 本项目是依赖库或SDK模式，所以重在结构的设计、类的抽象封装能力、Spring的扩展
* 如果某些dependency需要调用方决定的话，一定不要在module内直接依赖，可用`<optional>true</optional>`或类似方式解决。
* 所有依赖和版本号都在父pom中定义，在子module的pom中真实依赖
