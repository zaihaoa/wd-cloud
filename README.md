# wd-cloud

wd-cloud 是一个基于 Spring Boot 和 Spring Cloud Alibaba 的 Java 微服务后端。项目包含 API 网关、登录与权限管理、用户和角色管理、菜单管理以及分布式任务调度，也提供示例服务和 MyBatis-Plus 代码生成工具。

## 技术栈

| 用途 | 技术 |
| --- | --- |
| 开发与构建 | JDK 25、Maven |
| 应用框架 | Spring Boot 4.1.2、Spring Cloud 2025.1.4、Spring Cloud Alibaba 2025.1.0.0 |
| 注册与配置 | Nacos |
| API 网关 | Spring Cloud Gateway（WebFlux） |
| 服务间调用 | Spring Cloud OpenFeign |
| 登录与鉴权 | Sa-Token 1.46.0 |
| 数据访问 | MySQL、MyBatis-Plus 3.5.18.1、Druid 1.2.28 |
| 缓存 | Redis、Spring Data Redis、Redisson 4.8.0 |
| API 文档 | springdoc-openapi 3.2.0（OpenAPI 3 和 Swagger UI） |
| 任务调度 | XXL-JOB 3.5.0（调度中心和执行器核心随仓库提供） |
| 云服务集成 | 腾讯云 COS 对象存储、腾讯云 CLS 日志采集 |

依赖版本以[根 POM](pom.xml)和各模块的 `pom.xml` 为准。

## 模块结构

| 模块 | 职责 |
| --- | --- |
| [`wd-gateway`](wd-gateway/) | API 入口，负责路由转发和访问控制 |
| [`wd-system`](wd-system/) | 登录、用户、角色、权限、菜单及系统配置 |
| [`wd-job-admin`](wd-job-admin/) | XXL-JOB 调度中心 |
| [`wd-demo`](wd-demo/) | 示例服务 |
| [`wd-common`](wd-common/) | 公共组件，包括 Web、OpenFeign、Redis、MyBatis-Plus、日志、对象存储和任务执行器核心 |
| [`wd-generator`](wd-generator/) | 基于 MyBatis-Plus 和 FreeMarker 的代码生成工具 |

`wd-system` 和 `wd-demo` 各自包含 `*-api` 与 `*-server` 子模块，分别存放跨服务接口及共享模型、服务端实现。

## 本地开发

### 构建

安装 JDK 25 和 Maven，用 `mvn -version` 确认 Maven 正在使用 JDK 25，然后从仓库根目录打包。

```bash
mvn -DskipTests package
```

### 准备运行配置

1. **MySQL**。创建系统服务使用的数据库，并在该库中执行 [`mysql_table_ddl.sql`](docs/db/mysql_table_ddl.sql)。运行任务调度中心时，再执行 [`tables_xxl_job.sql`](docs/db/tables_xxl_job.sql) 初始化调度库。
2. **Nacos**。设置 `NACOS_SERVER_ADDR`、`NACOS_NAMESPACE` 和 `NACOS_CONFIG_GROUP`。启用鉴权时设置 `NACOS_USERNAME` 和 `NACOS_PASSWORD`。按各服务 `application.yml` 中的 `spring.config.import` 在 Nacos 创建配置，填写服务端口、数据源、Redis、Sa-Token 和网关路由等本地参数。
3. **Redis**。启动 Redis，在系统服务和网关读取的配置中填写连接信息。

### 启动服务

在 IDE 中导入根 `pom.xml`，为运行配置设置 JDK 25、VM 参数 `--enable-preview` 以及本地 Nacos 环境变量。调试系统接口时先启动系统服务，需要经过网关访问时再启动网关。示例服务和调度中心按需启动。

| 服务 | 启动类 | 配置入口 |
| --- | --- | --- |
| 系统服务 | `com.wd.SystemApplication` | [`wd-system-server/application.yml`](wd-system/wd-system-server/src/main/resources/application.yml) |
| 网关 | `com.wd.GatewayApplication` | [`wd-gateway-server/application.yml`](wd-gateway/wd-gateway-server/src/main/resources/application.yml) |
| 任务调度中心 | `com.xxl.job.admin.JobAdminApplication` | [`wd-job-admin/application.yml`](wd-job-admin/src/main/resources/application.yml) |
| 示例服务 | `com.wd.DemoApplication` | [`wd-demo-server/application.yml`](wd-demo/wd-demo-server/src/main/resources/application.yml) |

示例服务的默认 Nacos 命名空间和服务发现分组与网关不同。需要通过网关转发时，让两者使用相同的 `NACOS_NAMESPACE` 和 `NACOS_DISCOVERY_GROUP`，并配置对应路由。

系统服务和示例服务通过 springdoc-openapi 提供接口文档，访问地址取决于各服务的端口、上下文路径和访问控制配置。

## 贡献

欢迎通过 Issue 反馈问题或提出建议，也欢迎提交 Pull Request。反馈问题时请注明涉及的模块、JDK 版本和复现步骤，并对日志中的敏感信息做脱敏处理。

## 许可证

项目许可证待确定。仓库包含 XXL-JOB 的调度中心、执行器核心源码和初始化脚本。
