# 全局配置服务实施计划

**目标**　补齐 `GlobalConfigService` 的读写行为，新增、编辑和删除写入 `sys_common_log`，事务提交后清除全局配置的 Redis 缓存。

**依据**　现有全局配置接口与表结构、工作区的 DTO 修改，以及同模块的 `RoleServiceImpl`、`MenuServiceImpl` 和 `CommonLogService`。沿用 Spring 事务、MyBatis-Plus、Mapper XML 和 `RedisHelper`。

## 1. 补齐读写方法

- [x] 在 `wd-system/wd-system-server/src/main/java/com/wd/system/basic/service/impl/GlobalConfigServiceImpl.java` 补齐 `addGlobalConfig` 和 `updateGlobalConfig`。新增校验编码非空并检查有效记录中的重复编码，沿用数据库的 `uk_code` 约束处理并发冲突。新增固定为 `NORMAL`，缺省值和备注按表默认值写入空字符串，缺省排序为 `0`。
- [x] 编辑按 ID 查询并锁定有效记录，更新 `code`、`value`、`remark` 和 `sortNumber`，保留原类型。建议缺省字段沿用原值，显式传入空字符串可清空值或备注。编码变更时校验重名并排除自身。并发编辑与删除按同一行锁串行化，使审计结果对应实际写入状态。
- [x] `deleteGlobalConfigById` 保留 `SYSTEM` 禁删规则，逻辑删除时将记录 ID 写入 `delete_flag`，并限定待修改记录仍然有效。这样历史记录可保留，同一编码可以在删除后重新创建。
- [x] 在 `wd-system/wd-system-server/src/main/resources/mapper/basic/GlobalConfigMapper.xml` 的公共分页条件加入 `delete_flag = 0`，让总数和列表都排除已删除数据。参照 `RoleMapper.xml` 用固定列和排序方向生成排序语句，支持现有字段及驼峰别名，并按 ID 稳定分页。详情查不到继续返回 `null`，存在断言继续沿用 `data.not.exist`。

## 2. 在同一事务内记录增删改日志

- [x] 在 `wd-system/wd-system-server/src/main/java/com/wd/system/basic/domain/enums/CommonLogBusinessTypeEnum.java` 增加 `GLOBAL_CONFIG`。由 `GlobalConfigServiceImpl` 调用 `CommonLogService.addLog(CommonLogAddDTO)`，业务键使用配置 ID，操作类型沿用 `新增`、`编辑`、`删除`。
- [x] 新增和删除记配置编码，编辑记实际变动的字段名。建议配置值和备注的原文均不进入审计日志，同时移除现有的完整写入请求日志。保留明确的空编辑语义，提交相同字段值时按成功的空操作返回。
- [x] 配置实际写入成功才添加操作日志。日志写入失败时抛出业务异常并回滚配置写入，保证业务数据和审计记录对应。

## 3. 接入读取缓存并在提交后失效

- [x] 在 `wd-common/wd-common-redis/src/main/java/com/wd/common/redis/CacheRegion.java` 定义独立区域 `GLOBAL_CONFIG("global_config", "全局配置")`。当前仓库未定义该区域，也未找到已有的全局配置缓存读取。
- [x] 在 `wd-system/wd-system-server/src/main/java/com/wd/system/basic/service/GlobalConfigService.java` 增加 `String getValueByCode(String code)` 并实现。缓存按编码保存有效配置值，未命中时查库，查无记录返回 `null`。缓存设置过期时间，并区分空字符串配置与查无记录。
- [x] 核对 `wd-system/wd-system-api/src/main/java/com/wd/system/system/api/basic/GlobalConfigApi.java` 声明和仓库中的网关配置。沿用同模块 Feign 控制器的写法补上 `GET /feign/global-config/get` 的服务端入口，沿用 `code` 请求参数和 `R<String>` 返回结构。
- [x] 参照 `MenuServiceImpl` 在实际新增、编辑和删除成功后，通过 `TransactionSynchronization.afterCommit()` 切换版本并清除 `GLOBAL_CONFIG` 区域下的值缓存。回滚不触发清理。并发回填的旧值仍属于旧版本，后续读取会跳过。使用版本隔离可避免缓存锁与 JDBC 连接池之间的循环等待。

## 4. 验证

- [x] 从仓库根目录运行 `mvn -pl wd-system/wd-system-server -am compile`，检查系统服务及其上游模块。检查 `GlobalConfigApi` 的仓库内引用，目前只找到 demo 的注释示例。
- [x] 系统服务模块的 29 个测试通过，其中 5 个覆盖全局配置。
- [ ] 在有 Nacos、MySQL 和 Redis 的环境中验证新增默认类型、重复编码、编辑缺省字段、逻辑删除、删除后重建同编码、系统配置禁删，分页总数与详情的一致性，以及部署环境的 Feign 网关访问范围。
- [ ] 预热缓存后分别新增、编辑、改编码、删除，核对日志业务类型及配置 ID，并确认读取结果更新、旧编码失效且其他缓存区域保留。模拟日志写入失败，确认配置回滚且不清缓存。并发执行缓存未命中读取与编辑，核对编辑提交后的重新查询结果。
