# AGENTS.md

## 沟通

- 默认使用简体中文。代码、命令、路径和 API 名称保持原文。
- 回复中的本地文件链接使用绝对路径。需要指出位置时，在链接旁写行号。

## 项目与模块

- 本仓库是 Maven 多模块 Spring Cloud Alibaba 项目。JDK、编译参数和依赖版本以根 `pom.xml` 为准。
- `wd-system` 管理登录、用户、角色和菜单。`wd-gateway` 负责网关路由和过滤。公共组件位于 `wd-common`。
- 跨服务调用的 Feign 接口及其共享模型放在对应的 `*-api`。业务实现、Controller、Service 和 Mapper 放在对应的 `*-server`。跨服务依赖使用对方的 `*-api`。
- 修改功能前，先找同模块里最接近的实现。系统管理类 CRUD 可参考 `MenuController`、`MenuServiceImpl` 和 `MenuMapper.xml`。

## 开发约定

- Controller 负责参数校验、权限检查和响应转换。业务规则及多步数据库写入的事务边界放在 Service。数据访问沿用现有 MyBatis-Plus 和 Mapper XML 写法。
- 复用现有 `R<T>`、全局异常处理和 Sa-Token 权限机制。新增受保护接口时，参考同类接口配置权限。
- 修改 HTTP 或 Feign 接口时，检查调用方和网关路由。除非任务明确要求修改契约，保留既有路径、请求方法、字段含义、响应结构和权限要求。
- 涉及表结构变化时，检查 `docs/db/` 下的建表和变更脚本。为已有数据库提供增量 SQL，并说明旧数据如何兼容。
- 修改配置时，检查对应模块的 `application.yml`、Nacos 导入项和环境覆盖关系。示例配置使用占位值，日志中不得输出凭据。

## 验证与交付

- 从仓库根目录优先编译受影响模块及其上游依赖。系统服务示例为 `mvn -pl wd-system/wd-system-server -am compile`。
- 修改 `*-api`、公共模块或网关契约时，额外检查受影响的下游调用方。
- 交付时说明实际执行的验证及结果。需要 Nacos、MySQL 或 Redis 才能验证的行为，注明尚未验证的部分。
