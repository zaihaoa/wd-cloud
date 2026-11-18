package com.wd.system.basic.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.enums.FieldPermissionEnum;
import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.SelectCombobox;
import com.wd.common.core.objectcompare.ObjectCompareUtil;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.common.core.util.I18nUtil;
import com.wd.common.mybatisplus.PageUtil;
import com.wd.common.mybatisplus.SuperMapper;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.RoleConverter;
import com.wd.system.basic.domain.dto.*;
import com.wd.system.basic.domain.entity.Menu;
import com.wd.system.basic.domain.entity.Role;
import com.wd.system.basic.domain.entity.RolePermissionRelevance;
import com.wd.system.basic.domain.entity.User;
import com.wd.system.basic.domain.enums.*;
import com.wd.system.basic.domain.model.MenuButton;
import com.wd.system.basic.domain.vo.FieldPermissionGroupVO;
import com.wd.system.basic.domain.vo.FieldPermissionItemVO;
import com.wd.system.basic.domain.vo.RoleDetailVO;
import com.wd.system.basic.domain.vo.RolePageVO;
import com.wd.system.basic.mapper.MenuMapper;
import com.wd.system.basic.mapper.RoleMapper;
import com.wd.system.basic.mapper.RolePermissionRelevanceMapper;
import com.wd.system.basic.mapper.UserMapper;
import com.wd.system.basic.service.CommonLogService;
import com.wd.system.basic.service.RoleService;
import com.wd.system.basic.service.UserService;
import com.wd.system.basic.util.MenuButtonHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleConverter roleConverter;
    @Resource
    private RedisHelper redisHelper;
    @Resource
    private RolePermissionRelevanceMapper rolePermissionRelevanceMapper;
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private CommonLogService commonLogService;

    @Override
    public PageInfo<RolePageVO> pageList(RolePageParamDTO pageParam) {

        int count = baseMapper.pageCount(pageParam);
        if (count <= 0) {
            return PageUtil.buildPageInfo(pageParam);
        }

        List<RolePageDTO> dbRecords = baseMapper.pageList(pageParam);

        List<RolePageVO> voRecords = convertRolePageVOList(dbRecords);

        return PageUtil.buildPageInfo(pageParam, count, voRecords);
    }

    private List<RolePageVO> convertRolePageVOList(List<RolePageDTO> dbRecords) {
        Map<Long, String> userIdNameMap = userService.getUserIdNameMap();

        return dbRecords
                .stream()
                .map(record -> {
                    RolePageVO vo = roleConverter.convertRolePageVO(record);

                    vo.setTypeDesc(CodeDescEnumUtil.getDescByCode(RoleTypeEnum.class, vo.getType()));
                    vo.setStatusDesc(CodeDescEnumUtil.getDescByCode(RoleStatusEnum.class, vo.getStatus()));

                    vo.setCreateUserName(userIdNameMap.get(vo.getCreateUserId()));
                    vo.setUpdateUserName(userIdNameMap.get(vo.getUpdateUserId()));

                    return vo;
                })
                .toList();
    }

    @Override
    public RoleDetailVO detail(Long id) {
        Role role = getRoleById(id);
        if (Objects.isNull(role)) {
            return null;
        }
        RoleDetailVO vo = roleConverter.convertRoleDetailVO(role);

        vo.setTypeDesc(CodeDescEnumUtil.getDescByCode(RoleTypeEnum.class, vo.getType()));
        vo.setStatusDesc(CodeDescEnumUtil.getDescByCode(RoleStatusEnum.class, vo.getStatus()));

        Map<Long, String> userIdNameMap = userService.getUserIdNameMap();

        vo.setCreateUserName(userIdNameMap.get(vo.getCreateUserId()));
        vo.setUpdateUserName(userIdNameMap.get(vo.getUpdateUserId()));

        vo.setPermissionFlag(role.getPermissionFlag() == null ? List.of() : new ArrayList<>(role.getPermissionFlag()));

        List<RolePermissionRelevance> permissions = rolePermissionRelevanceMapper.selectList(
                Wrappers.<RolePermissionRelevance>lambdaQuery()
                        .eq(RolePermissionRelevance::getRoleId, id)
                        .orderByAsc(RolePermissionRelevance::getId));

        vo.setMenuIds(permissions.stream()
                .filter(p -> RolePermissionTypeEnum.MENU.getCode().equals(p.getType()))
                .map(RolePermissionRelevance::getRelevanceId)
                .filter(Objects::nonNull)
                .distinct().toList());
        vo.setButtonCodes(permissions.stream()
                .filter(p -> RolePermissionTypeEnum.BUTTON.getCode().equals(p.getType()))
                .map(RolePermissionRelevance::getRelevanceValue)
                .filter(StringUtils::isNotBlank)
                .distinct().toList());
        vo.setFieldCodes(permissions.stream()
                .filter(p -> RolePermissionTypeEnum.FIELD.getCode().equals(p.getType()))
                .map(RolePermissionRelevance::getRelevanceValue)
                .filter(StringUtils::isNotBlank)
                .distinct().toList());
        vo.setDataUserIds(permissions.stream()
                .filter(p -> RolePermissionTypeEnum.DATA_USER.getCode().equals(p.getType()))
                .map(RolePermissionRelevance::getRelevanceId)
                .filter(userId -> userId != null && userId > 0 && userIdNameMap.containsKey(userId))
                .distinct().toList());
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean authorizeAll(RoleAuthorizeAllDTO dto) {
        AssertUtil.notNull(dto.getAuthorized(), "授权操作不能为空");
        AssertUtil.isTrue(CodeDescEnumUtil.isExist(RolePermissionFlagEnum.class, dto.getPermissionFlag()), "权限标记不正确");

        Role role = lockRole(dto.getRoleId());
        Set<String> flags = new LinkedHashSet<>(Optional.ofNullable(role.getPermissionFlag()).orElse(List.of()));
        boolean grant = dto.getAuthorized();
        String flag = dto.getPermissionFlag();
        boolean changed = grant ? flags.add(flag) : flags.remove(flag);
        List<String> permissionFlags = new ArrayList<>(flags);
        if (Objects.equals(role.getPermissionFlag(), permissionFlags)) {
            return true;
        }

        // 仅更新权限标记及自动填充的审计字段。
        Role update = new Role();
        update.setId(role.getId());
        update.setPermissionFlag(permissionFlags);
        AssertUtil.isTrue(baseMapper.updateById(update) > 0, "角色授权失败");
        if (changed) {
            String name = CodeDescEnumUtil.getDescByCode(RolePermissionFlagEnum.class, flag);
            addRoleLog(role.getId(), grant ? "授权" : "取消授权", (grant ? "授予" : "取消") + name);
        }
        clearPermissionCacheAfterCommit();
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean authorizeMenu(RoleAuthorizeMenuDTO dto) {
        Long roleId = dto.getRoleId();
        Set<Long> menuIds = new LinkedHashSet<>(dto.getMenuIds());
        Set<String> buttonCodes = new LinkedHashSet<>(dto.getButtonCodes());
        RolePermissionTypeEnum menuType = RolePermissionTypeEnum.MENU;
        RolePermissionTypeEnum buttonType = RolePermissionTypeEnum.BUTTON;

        lockRole(roleId);
        Map<Long, Menu> menus = loadMenus(menuIds);
        AssertUtil.isTrue(menus.size() == menuIds.size(), "菜单不存在");
        validateButtonCodes(menus.values(), buttonCodes);

        Set<Long> oldMenuIds = savedPermissions(roleId, menuType, RolePermissionRelevance::getRelevanceId);
        Set<String> oldButtonCodes = savedPermissions(roleId, buttonType, RolePermissionRelevance::getRelevanceValue);
        PermissionChanges changes = new PermissionChanges();
        if (!oldMenuIds.equals(menuIds) || !oldButtonCodes.equals(buttonCodes)) {
            Map<Long, Menu> oldMenus = oldMenuIds.equals(menuIds) ? menus : loadMenus(oldMenuIds);
            changes.add(menuType, oldMenuIds, menuIds,
                    id -> menuName(oldMenus.get(id)), id -> menuName(menus.get(id)));
            changes.add(buttonType, oldButtonCodes, buttonCodes,
                    buttonNames(oldMenus.values()), buttonNames(menus.values()));
        }
        replacePermissions(roleId, menuType, menuIds, Set.of());
        replacePermissions(roleId, buttonType, Set.of(), buttonCodes);
        finishAuthorization(roleId, changes);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean authorizeField(RoleAuthorizeFieldDTO dto) {
        Long roleId = dto.getRoleId();
        Set<String> fieldCodes = new LinkedHashSet<>(dto.getFieldCodes());
        RolePermissionTypeEnum type = RolePermissionTypeEnum.FIELD;

        lockRole(roleId);
        for (String code : fieldCodes) {
            AssertUtil.isTrue(StringUtils.isNotBlank(code)
                    && CodeDescEnumUtil.isExist(FieldPermissionEnum.class, code), "字段权限编码不存在");
        }
        Set<String> oldCodes = savedPermissions(roleId, type, RolePermissionRelevance::getRelevanceValue);
        Function<String, String> names = code -> CodeDescEnumUtil.getDescByCode(FieldPermissionEnum.class, code);
        PermissionChanges changes = new PermissionChanges();
        changes.add(type, oldCodes, fieldCodes, names, names);
        replacePermissions(roleId, type, Set.of(), fieldCodes);
        finishAuthorization(roleId, changes);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean authorizeDataUser(RoleAuthorizeDataUserDTO dto) {
        Long roleId = dto.getRoleId();
        RoleAuthorizeOperationEnum operation = dto.getOperation();
        AssertUtil.notNull(operation, "用户数据授权操作不能为空");
        Set<Long> userIds = new LinkedHashSet<>(dto.getUserIds());

        lockRole(roleId);
        boolean grant = operation == RoleAuthorizeOperationEnum.GRANT;
        Set<Long> changedIds = updateUserPermissions(roleId, userIds, grant);
        Map<Long, String> names = loadUserNames(changedIds);
        PermissionChanges changes = new PermissionChanges();
        changes.add(RolePermissionTypeEnum.DATA_USER, grant ? Set.of() : changedIds, grant ? changedIds : Set.of(),
                names::get, names::get);
        finishAuthorization(roleId, changes);
        return true;
    }

    private void finishAuthorization(Long roleId, PermissionChanges changes) {
        if (!changes.isEmpty()) {
            addRoleLog(roleId, changes.operationType(), changes.content());
        }
        clearPermissionCacheAfterCommit();
    }

    private Set<Long> updateUserPermissions(Long roleId, Set<Long> userIds, boolean grant) {
        RolePermissionTypeEnum type = RolePermissionTypeEnum.DATA_USER;
        List<List<Long>> batches = CollUtil.split(new ArrayList<>(userIds), SuperMapper.DEFAULT_BATCH_SIZE);
        if (grant) {
            for (List<Long> batch : batches) {
                long count = userMapper.selectCount(Wrappers.<User>lambdaQuery().in(User::getId, batch));
                AssertUtil.isTrue(count == batch.size(), "用户不存在或已删除");
            }
        }

        Set<Long> changedIds = new LinkedHashSet<>();
        for (List<Long> batch : batches) {
            var query = permissionQuery(roleId, type).in(RolePermissionRelevance::getRelevanceId, batch);
            Set<Long> existingIds = rolePermissionRelevanceMapper.selectList(query).stream()
                    .map(RolePermissionRelevance::getRelevanceId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (!grant) {
                if (rolePermissionRelevanceMapper.delete(query) > 0) {
                    changedIds.addAll(existingIds);
                }
                continue;
            }

            Set<Long> missingIds = new LinkedHashSet<>(batch);
            missingIds.removeAll(existingIds);
            savePermissions(roleId, type, missingIds, Set.of());
            changedIds.addAll(missingIds);
        }
        return changedIds;
    }

    private void validateButtonCodes(Collection<Menu> menus, Set<String> codes) {
        if (codes.isEmpty()) {
            return;
        }
        Set<String> allowed = menus.stream()
                .flatMap(menu -> MenuButtonHelper.permissionCodes(menu.getButton()))
                .collect(Collectors.toSet());
        AssertUtil.isTrue(allowed.containsAll(codes), "按钮权限编码无效或不属于已选菜单");
    }

    private <T> Set<T> savedPermissions(Long roleId, RolePermissionTypeEnum type,
                                        Function<RolePermissionRelevance, T> key) {
        return rolePermissionRelevanceMapper.selectList(permissionQuery(roleId, type))
                .stream().map(key).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Map<Long, Menu> loadMenus(Set<Long> menuIds) {
        Map<Long, Menu> menus = new HashMap<>();
        for (List<Long> batch : CollUtil.split(new ArrayList<>(menuIds), SuperMapper.DEFAULT_BATCH_SIZE)) {
            for (Menu menu : menuMapper.selectList(Wrappers.<Menu>lambdaQuery().in(Menu::getId, batch))) {
                menus.put(menu.getId(), menu);
            }
        }
        return menus;
    }

    private Map<Long, String> loadUserNames(Set<Long> userIds) {
        Map<Long, String> names = new HashMap<>();
        for (List<Long> batch : CollUtil.split(new ArrayList<>(userIds), SuperMapper.DEFAULT_BATCH_SIZE)) {
            for (User user : userMapper.selectNamesForRoleAudit(batch)) {
                names.put(user.getId(), user.getName());
            }
        }
        return names;
    }

    private static String menuName(Menu menu) {
        return menu == null ? null : menu.getMenuName();
    }

    private static Function<String, String> buttonNames(Collection<Menu> menus) {
        Map<String, Set<String>> namesByCode = new HashMap<>();
        for (Menu menu : menus) {
            if (StringUtils.isBlank(menu.getMenuName()) || menu.getButton() == null) {
                continue;
            }
            for (MenuButton button : menu.getButton()) {
                if (button != null && StringUtils.isNotBlank(button.getCode()) && StringUtils.isNotBlank(button.getLabel())) {
                    namesByCode.computeIfAbsent(button.getCode(), code -> new TreeSet<>())
                            .add(menu.getMenuName() + " / " + button.getLabel());
                }
            }
        }
        return code -> {
            Set<String> names = namesByCode.get(code);
            return names == null ? null : String.join("、", names);
        };
    }

    private static final class PermissionChanges {
        private final List<String> grants = new ArrayList<>();
        private final List<String> revocations = new ArrayList<>();

        private <T> void add(RolePermissionTypeEnum type, Set<T> before, Set<T> after,
                             Function<T, String> oldName, Function<T, String> newName) {
            append(grants, "授予" + type.getDesc(), after, before, newName);
            append(revocations, "取消" + type.getDesc(), before, after, oldName);
        }

        private static <T> void append(List<String> changes, String action, Set<T> candidates, Set<T> retained,
                                       Function<T, String> resolveName) {
            List<String> names = new ArrayList<>();
            int unavailable = 0;
            for (T key : candidates) {
                if (retained.contains(key)) {
                    continue;
                }
                String name = resolveName.apply(key);
                if (StringUtils.isBlank(name)) {
                    unavailable++;
                } else {
                    names.add(name);
                }
            }
            names.sort(Comparator.naturalOrder());
            if (unavailable > 0) {
                names.add("名称已不可用（" + unavailable + "项）");
            }
            if (!names.isEmpty()) {
                changes.add(action + "：" + String.join("、", names));
            }
        }

        private boolean isEmpty() {
            return grants.isEmpty() && revocations.isEmpty();
        }

        private String operationType() {
            return grants.isEmpty() ? "取消授权" : "授权";
        }

        private String content() {
            List<String> changes = new ArrayList<>(grants);
            changes.addAll(revocations);
            return String.join("\n", changes);
        }
    }

    private void replacePermissions(Long roleId, RolePermissionTypeEnum type, Set<Long> ids, Set<String> values) {
        rolePermissionRelevanceMapper.delete(permissionQuery(roleId, type));
        savePermissions(roleId, type, ids, values);
    }

    private LambdaQueryWrapper<RolePermissionRelevance> permissionQuery(
            Long roleId, RolePermissionTypeEnum type) {
        return Wrappers.<RolePermissionRelevance>lambdaQuery()
                .eq(RolePermissionRelevance::getRoleId, roleId)
                .eq(RolePermissionRelevance::getType, type.getCode());
    }

    private void savePermissions(Long roleId, RolePermissionTypeEnum type, Set<Long> ids, Set<String> values) {
        List<RolePermissionRelevance> permissions = new ArrayList<>(ids.size() + values.size());
        for (Long relevanceId : ids) {
            permissions.add(newPermission(roleId, type.getCode(), relevanceId, ""));
        }
        for (String relevanceValue : values) {
            permissions.add(newPermission(roleId, type.getCode(), 0L, relevanceValue));
        }
        insertPermissions(permissions);
    }

    private void insertPermissions(List<RolePermissionRelevance> permissions) {
        for (List<RolePermissionRelevance> batch : CollUtil.split(permissions, SuperMapper.DEFAULT_BATCH_SIZE)) {
            AssertUtil.isTrue(rolePermissionRelevanceMapper.insertBatchSomeColumn(batch) == batch.size(), "保存角色授权失败");
        }
    }

    private Role lockRole(Long id) {
        AssertUtil.isTrue(id != null && id > 0, "角色ID必须为正数");
        Role role = selectRoleForUpdate(id);
        AssertUtil.notNull(role, "角色不存在或已删除");
        return role;
    }

    private Role selectRoleForUpdate(Long id) {
        return baseMapper.selectOne(Wrappers.<Role>lambdaQuery().eq(Role::getId, id).last("FOR UPDATE"));
    }

    private Role getRoleForUpdateAssertExist(Long id) {
        Role role = id == null ? null : selectRoleForUpdate(id);
        AssertUtil.notNull(role, () -> I18nUtil.message("data.not.exist"));
        return role;
    }

    private RolePermissionRelevance newPermission(Long roleId, String type,
                                                  Long relevanceId, String relevanceValue) {
        RolePermissionRelevance permission = new RolePermissionRelevance();
        permission.setId(IdWorker.getId());
        permission.setRoleId(roleId);
        permission.setType(type);
        permission.setRelevanceId(relevanceId);
        permission.setRelevanceValue(relevanceValue);
        return permission;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long addRole(RoleAddDTO roleAddParam) {
        log.info("新增角色,参数:{}", JSON.toJSONString(roleAddParam));

        Role role = insertRole(roleAddParam, null);
        addRoleLog(role.getId(), "新增", "新增角色：" + role.getCode() + " / " + role.getName());
        clearPermissionCacheAfterCommit();
        return role.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long copyRole(RoleCopyDTO roleCopyParam) {
        Role source = lockRole(roleCopyParam.getRoleId());
        Role role = insertRole(roleCopyParam, source.getPermissionFlag());
        List<RolePermissionRelevance> permissions = rolePermissionRelevanceMapper.selectList(
                        Wrappers.<RolePermissionRelevance>lambdaQuery()
                                .eq(RolePermissionRelevance::getRoleId, source.getId()))
                .stream()
                .map(permission -> newPermission(role.getId(), permission.getType(),
                        permission.getRelevanceId(), permission.getRelevanceValue()))
                .toList();
        insertPermissions(permissions);

        addRoleLog(role.getId(), "复制", "从角色 %s（%s）复制".formatted(source.getName(), source.getCode()));
        clearPermissionCacheAfterCommit();
        return role.getId();
    }

    private Role insertRole(RoleAddDTO roleAddParam, List<String> permissionFlag) {
        checkUnique(roleAddParam.getCode(), roleAddParam.getName(), null);

        Role role = new Role();
        role.setId(IdWorker.getId());
        role.setCode(roleAddParam.getCode());
        role.setName(roleAddParam.getName());
        role.setStatus(RoleStatusEnum.ENABLED.getCode());
        role.setType(RoleTypeEnum.NORMAL.getCode());
        role.setRemark(Optional.ofNullable(roleAddParam.getRemark()).orElse(""));
        role.setPermissionFlag(permissionFlag);
        role.setDeleteFlag(CommonConstant.NOT_DELETE_FLAG);

        AssertUtil.isTrue(baseMapper.insert(role) > 0, "新增角色失败");
        return role;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRole(RoleUpdateDTO roleUpdateParam) {
        log.info("更新角色,参数:{}", JSON.toJSONString(roleUpdateParam));

        Role role = getRoleForUpdateAssertExist(roleUpdateParam.getId());
        checkUnique(roleUpdateParam.getCode(), roleUpdateParam.getName(), role.getId());
        String remark = Optional.ofNullable(roleUpdateParam.getRemark()).orElse("");

        String compareLog = ObjectCompareUtil.compareLog(role, roleUpdateParam);

        baseMapper.update(Wrappers.<Role>lambdaUpdate()
                .set(Role::getCode, roleUpdateParam.getCode())
                .set(Role::getName, roleUpdateParam.getName())
                .set(Role::getRemark, remark)
                .eq(Role::getId, role.getId()));

        if (StringUtils.isNotBlank(compareLog)) {
            addRoleLog(role.getId(), "编辑", compareLog);
        }

        clearPermissionCacheAfterCommit();

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateStatus(RoleUpdateStatusDTO updateStatusDTO) {
        log.info("更新角色状态,参数:{}", JSON.toJSONString(updateStatusDTO));

        Role role = getRoleForUpdateAssertExist(updateStatusDTO.getId());
        String status = updateStatusDTO.getStatus();
        if (Objects.equals(role.getStatus(), status)) {
            return true;
        }

        boolean success = baseMapper.update(Wrappers.<Role>lambdaUpdate()
                .set(Role::getStatus, status)
                .eq(Role::getId, role.getId())) > 0;
        if (success) {
            String oldStatus = CodeDescEnumUtil.getDescByCode(RoleStatusEnum.class, role.getStatus());
            String newStatus = CodeDescEnumUtil.getDescByCode(RoleStatusEnum.class, status);
            addRoleLog(role.getId(), "编辑", "角色状态：[%s]变更为[%s]".formatted(oldStatus, newStatus));
        }

        clearPermissionCacheAfterCommit();
        return success;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteRoleById(IdDTO idParam) {
        log.info("删除角色,参数:{}", JSON.toJSONString(idParam));
        Long id = idParam.getId();

        // 判断数据是否存在
        Role role = getRoleForUpdateAssertExist(id);

        // 逻辑删除使用角色ID作为删除标记，允许重新使用已删除角色的编码和名称。
        boolean success = baseMapper.deleteById(role) > 0;
        if (success) {
            addRoleLog(role.getId(), "删除", "删除角色：" + role.getCode() + " / " + role.getName());
        }

        clearPermissionCacheAfterCommit();
        return success;
    }

    @Override
    public Role getRoleById(Serializable id) {
        if (id == null) {
            return null;
        }
        return baseMapper.selectById(id);
    }

    @Override
    public Role getRoleByIdAssertExist(Serializable id) {
        Role record = getRoleById(id);
        AssertUtil.notNull(record, () -> I18nUtil.message("data.not.exist"));
        return record;
    }

    private void checkUnique(String code, String name, Long excludeId) {
        long codeCount = baseMapper.selectCount(Wrappers.<Role>lambdaQuery()
                .eq(Role::getCode, code)
                .ne(excludeId != null, Role::getId, excludeId));
        AssertUtil.isTrue(codeCount == 0, "角色编码已存在");

        long nameCount = baseMapper.selectCount(Wrappers.<Role>lambdaQuery()
                .eq(Role::getName, name)
                .ne(excludeId != null, Role::getId, excludeId));
        AssertUtil.isTrue(nameCount == 0, "角色名称已存在");
    }

    private void addRoleLog(Long roleId, String operationType, String content) {
        CommonLogAddDTO entry = CommonLogAddDTO.builder()
                .businessType(CommonLogBusinessTypeEnum.ROLE.getCode())
                .businessKey(String.valueOf(roleId))
                .operationType(operationType)
                .content(content)
                .build();
        AssertUtil.isTrue(commonLogService.addLog(entry), "写入角色操作日志失败");
    }

    private void clearPermissionCache() {
        redisHelper.deleteByCacheRegion(CacheRegion.PERMISSION);
    }

    private void clearPermissionCacheAfterCommit() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            clearPermissionCache();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                clearPermissionCache();
            }
        });
    }

    @Override
    public List<SelectCombobox> roleCombobox(String status) {
        // 角色ID和名称
        return baseMapper
                .selectList(Wrappers.<Role>lambdaQuery()
                        .eq(StringUtils.isNotBlank(status), Role::getStatus, status)
                        .select(Role::getId, Role::getName))
                .stream()
                .map(v -> new SelectCombobox(String.valueOf(v.getId()), v.getName()))
                .sorted(Comparator.comparing(SelectCombobox::getLabel))
                .toList();
    }

    @Override
    public List<FieldPermissionGroupVO> fieldPermissionConstants() {
        Map<String, List<FieldPermissionItemVO>> permissionGroups = Arrays.stream(FieldPermissionEnum.values())
                .collect(Collectors.groupingBy(FieldPermissionEnum::getGroup, LinkedHashMap::new,
                        Collectors.mapping(permission -> new FieldPermissionItemVO(permission.getCode(), permission.getDesc()),
                                Collectors.toList())));

        return permissionGroups.entrySet()
                .stream()
                .map(entry -> new FieldPermissionGroupVO(entry.getKey(), entry.getValue()))
                .toList();
    }
}
