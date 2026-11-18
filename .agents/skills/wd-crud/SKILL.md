---
name: wd-crud
description: 用于在 wd-cloud 的 Spring Boot Maven 模块中，根据 MySQL 表结构生成实体及管理端 CRUD，或补齐已有模块的分页、新增、编辑、删除、详情接口。
---

# wd-cloud 基础 CRUD

## 确定范围

先读仓库和目标模块的 `AGENTS.md`、`pom.xml`、表 DDL 及同模块的相近实现。核对主键、列类型、非空与默认值、唯一约束、审计列和删除策略。补齐已有功能时，先查 Controller、Service、Mapper、调用方和网关，保留已约定的路径、HTTP 方法、请求字段、权限和响应语义。

以 `docs/db/` 中的 DDL 或用户提供的表结构为依据。需要用仓库的 MyBatis-Plus `CodeGenerator` 时，明确目标表、模块、包名和输出目录，在临时目录生成后合入本次所需文件。生成器使用目标环境提供的连接信息，示例使用占位值。涉及表结构修改时，同步维护建表脚本和适用于已有数据库的增量 SQL。

## 实体与接口

- 实体放在 `domain.entity`。使用 Lombok `@Getter`、`@Setter`，以 `@TableName` 和 `@TableField` 对应物理表列，Java 属性使用驼峰命名。雪花主键映射为 `Long`，使用 `@TableId(type = IdType.ASSIGN_ID)`。表含标准创建人、更新人和时间列时继承 `BaseUserTimeEntity`。使用逻辑删除时，为标记字段配置 `@TableLogic`。
- Mapper 继承 `SuperMapper<实体>` 并使用 `@Mapper`。自定义 SQL 对应 `src/main/resources/mapper` 下同包约定的 XML，核对命名空间、语句 ID、入参名和结果类型。Service 接口继承 `SuperService<实体>`，实现继承 `ServiceImpl<Mapper, 实体>` 并使用 `@Service`。
- 将新增、编辑和分页入参及分页查询结果放在 `domain.dto`。对外的分页与详情结果放在 `domain.vo`，用 `@Mapper(componentModel = "spring")` 的 MapStruct Converter 转换。按页面需要补充枚举描述等展示字段，暴露客户端需要的字段。
- Controller 使用 `@RestController`、`@RequestMapping`、`R<T>` 和同模块的 Swagger 注解。新管理接口通常使用 `POST /page`、`POST /add`、`POST /update`、`POST /delete` 和 `GET /detail?id=...`，以同模块项目约定为准。受保护接口按操作配置 `@SaCheckPermission`，在项目的权限常量和菜单配置中登记实际权限。DTO 请求通过 `@Valid` 校验，字符串必填用 `@NotBlank`，主键等必填对象字段用 `@NotNull`。

## 分页与详情

- 分页入参继承 `BaseQuery`。自定义分页使用 `pageCount(@Param("param") ...)` 和 `pageList(@Param("param") ...)`。两条 SQL 共用同一 `<sql>` 过滤片段，包含有效记录条件及适用的数据权限条件。过滤值和分页参数用 `#{...}` 绑定。通过 XML `<choose>` 将前端排序字段与方向映射为固定 SQL，并以主键作为并列值的排序依据。
- Service 先查总数。总数为零时返回 `PageUtil.buildPageInfo(pageParam)`。其余情况查询当页记录，转为分页 VO，再用 `PageUtil.buildPageInfo(pageParam, count, records)` 返回 `PageInfo<分页VO>`。
- 详情使用 `selectById` 查询有效记录，经 Converter 返回详情 VO。根据接口约定处理查无记录的情况。编辑和删除等必须命中记录的操作可复用查找并断言的方法，通过现有业务异常或 `AssertUtil` 报错。

## 新增、编辑与删除

- **新增**。仅接收业务允许写入的字段，显式赋默认值、系统控制字段和有效标记。唯一字段可先查询有效记录给出业务提示，并由数据库唯一约束处理并发冲突。检查插入行数，再按接口返回新 ID。日志需要 ID 时可按仓库规则使用 `IdWorker.getId()` 或读取插入后回填的实体 ID。
- **编辑**。先查有效记录。规则或审计结果依赖旧值时，可在同一写事务中用 `FOR UPDATE` 锁定该行。按接口约定处理缺省、显式清空和默认值。缺省表示保留时，将旧值作为最终值。若显式 `null` 和缺省含义不同，使用项目的字段存在性表示区分两者。先算出最终写入值，再用旧实体和标有 `@ObjectCompare("字段名")` 的有效更新 DTO 执行 `ObjectCompareUtil.compareLog` 或 `compare`。只比较实际可编辑的字段。可修改的唯一字段检查重名时排除当前 ID。按实际更新结果处理响应与副作用，无有效变更时按接口语义返回。
- **删除**。单 ID 请求复用 `IdDTO`。检查记录是否存在、业务禁删条件及需要保护的关联数据。按表约定使用逻辑删除，并检查受影响行数。允许同一编码反复重建时，使用 `0` 标记有效记录，用行 ID 标记删除记录，并将可复用的业务键及 `delete_flag` 设为复合唯一索引，例如 `UNIQUE (code, delete_flag)`。已有业务键单独的唯一约束时，通过对应的增量 SQL 同步调整。检查逻辑删除会写入所需的更新审计列，尤其是仅传 Wrapper 的更新。

## 事务、日志与缓存

- 业务数据、关联数据和必要审计的写入在 Service 的同一事务内执行，沿用 `@Transactional(rollbackFor = Exception.class)`。写入成功后通过 `CommonLogService.addLog` 记录业务类型、记录 ID、操作类型和变更内容。必需审计写入失败时抛出业务异常回滚。凭据字段仅记录字段发生变化，示例和日志使用脱敏内容。
- 有按业务键读取的缓存时，在现有 `CacheRegion` 中直接使用该业务键读 Redis。未命中时查库，并按该业务的过期和空值策略回填。增删改成功后用事务提交回调执行失效，清理项目约定的缓存区域或受影响的键。缓存按编码且支持改编码时，失效范围要覆盖旧键和新键。
- 需要跨服务调用时，Feign 契约和共享模型放在对应的 `*-api`，服务端入口放在 `*-server`。核对调用方、网关和访问范围。

## 验证

从仓库根目录编译受影响模块及其上游依赖，例如 `mvn -pl wd-system/wd-system-server -am compile`。按实际改动核对必填字段、分页总数、排序、详情和逻辑删除。涉及唯一性、审计和缓存时，检查删除后重建、日志失败回滚、提交后缓存失效等行为。交付时区分已执行的检查与待 MySQL、Redis 或 Nacos 环境验证的行为。
