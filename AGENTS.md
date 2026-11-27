# AGENTS.md

## 技术栈与环境

| 用途 | 技术 |
| --- | --- |
| 开发与构建 | JDK 25、Maven 多模块 |
| 应用与服务治理 | Spring Boot 4、Spring Cloud、Spring Cloud Alibaba、Nacos |
| 网关与服务调用 | Spring Cloud Gateway（WebFlux）、Spring Cloud OpenFeign |
| 登录与鉴权 | Sa-Token |
| 持久化与缓存 | MySQL、MyBatis-Plus、Redis、Redisson |
| 模型转换与接口文档 | Lombok、MapStruct、springdoc-openapi |
| 任务调度 | XXL-JOB |

- 依赖版本和编译参数以 [根 POM](pom.xml) 与模块 POM 为准。用 `mvn -version` 确认 Maven 使用 JDK 25。在 IDE 直接运行服务时添加 JVM 参数 `--enable-preview`。
- 本地运行系统服务需要 Nacos、MySQL 和 Redis。配置从服务的 `src/main/resources/application.yml` 与 `spring.config.import` 追踪，启动与初始化步骤见 [README](README.md)。

## 项目结构

| 路径 | 职责 |
| --- | --- |
| [`wd-system/`](wd-system/) | 系统管理，包含登录、用户、角色、权限、菜单和系统配置 |
| [`wd-gateway/wd-gateway-server/`](wd-gateway/wd-gateway-server/) | API 网关，负责路由、过滤和访问控制 |
| [`wd-common/`](wd-common/) | 公共组件，包括 Web、Feign、MyBatis-Plus、Redis、日志和任务执行器 |
| [`wd-job-admin/`](wd-job-admin/) | XXL-JOB 调度中心 |
| [`wd-demo/`](wd-demo/) | 示例服务 |
| [`docs/db/`](docs/db/) | MySQL 建表脚本与增量 SQL |

- `wd-system` 和 `wd-demo` 按 `*-api` 与 `*-server` 划分。Feign 接口及其共享模型放在 `*-api`，服务端实现和业务代码放在 `*-server`。跨服务依赖对方的 `*-api`。
- 业务代码在服务模块的 `src/main/java/`，Mapper XML 在 `src/main/resources/mapper/`，测试在 `src/test/java/`。

## 构建与测试

在仓库根目录执行，优先验证受影响的模块及其上游依赖。

| 场景 | 命令 |
| --- | --- |
| 编译系统服务 | `mvn -pl wd-system/wd-system-server -am compile` |
| 编译网关 | `mvn -pl wd-gateway/wd-gateway-server -am compile` |
| 运行系统服务指定测试类，以菜单分类为例 | `mvn -pl wd-system/wd-system-server -am -Dtest=MenuGroupNameTest -Dsurefire.failIfNoSpecifiedTests=false test` |

修改 `*-api`、公共组件或网关契约后，额外检查受影响的下游模块。`-am` 构建所选模块的上游依赖，下游模块需按影响范围另行选择。

## 实现约定

- 先读目标模块里最接近的实现，沿实际入口追踪 Controller、Service、Mapper 和调用方。复用已有类型、方法和查询，保留工作区已有改动，将修改限制在本次需求范围内。
- Controller 负责请求校验、权限检查和响应封装，沿用 `@Valid`、`R<T>` 和全局异常处理。Service 负责业务规则和多步写入的事务边界，业务校验参考 `AssertUtil`。数据访问沿用现有 MyBatis-Plus 和 Mapper XML 的写法。
- 系统管理 CRUD 可参考 [MenuController](wd-system/wd-system-server/src/main/java/com/wd/system/basic/controller/MenuController.java)、[MenuServiceImpl](wd-system/wd-system-server/src/main/java/com/wd/system/basic/service/impl/MenuServiceImpl.java) 和 [MenuMapper.xml](wd-system/wd-system-server/src/main/resources/mapper/basic/MenuMapper.xml)。根据表结构新增或补齐 CRUD 时，查看 [wd-crud](.agents/skills/wd-crud/SKILL.md)。

## 接口、数据与配置变更

- **接口与权限**。修改 HTTP 或 Feign 接口时查找实际调用方，涉及网关转发时核对路由。除非任务明确要求改变契约，保留请求路径、方法、字段含义、响应结构和权限要求。新增受保护接口时，参考同类接口的 `@SaCheckPermission`、`PermissionConstant` 和数据权限条件。
- **数据库**。修改表结构时，检查 `docs/db/` 下的建表和历史变更脚本，同步维护建表定义，为已有库提供增量 SQL，并说明旧数据的默认值、回填或兼容方式。
- **事务与缓存**。修改带缓存的写入时，核对事务回滚及缓存失效范围。菜单写入可参考 `MenuServiceImpl.clearMenuCacheAfterCommit()`，在事务提交后清理菜单与权限缓存。
- **配置**。核对模块的 `application.yml`、Nacos 导入项和环境覆盖关系。示例使用占位值，日志中的敏感字段做脱敏处理。

## 交付检查

- 按实际改动验证行为。接口变更核对调用方与旧请求，权限变更核对允许和拒绝路径，数据库或缓存变更核对旧数据和提交后的读取结果。
- 交付时说明实际运行的命令及结果。需要 Nacos、MySQL 或 Redis 环境才能确认的行为，注明具体待验证项。
