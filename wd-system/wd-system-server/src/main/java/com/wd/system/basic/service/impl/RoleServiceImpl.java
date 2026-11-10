package com.wd.system.basic.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.enums.FieldPermissionEnum;
import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.common.core.util.I18nUtil;
import com.wd.common.mybatisplus.PageUtil;
import com.wd.common.mybatisplus.SuperMapper;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.RoleConverter;
import com.wd.system.basic.domain.dto.RoleAddDTO;
import com.wd.system.basic.domain.dto.RoleAuthorizeAllDTO;
import com.wd.system.basic.domain.dto.RoleAuthorizeDTO;
import com.wd.system.basic.domain.dto.RolePageDTO;
import com.wd.system.basic.domain.dto.RolePageParamDTO;
import com.wd.system.basic.domain.dto.RoleUpdateDTO;
import com.wd.system.basic.domain.dto.RoleUpdateStatusDTO;
import com.wd.system.basic.domain.entity.Role;
import com.wd.system.basic.domain.entity.Menu;
import com.wd.system.basic.domain.entity.User;
import com.wd.system.basic.domain.entity.RolePermissionRelevance;
import com.wd.system.basic.domain.enums.RolePermissionFlagEnum;
import com.wd.system.basic.domain.enums.RolePermissionTypeEnum;
import com.wd.system.basic.domain.enums.RoleStatusEnum;
import com.wd.system.basic.domain.enums.RoleTypeEnum;
import com.wd.system.basic.domain.vo.RolePageVO;
import com.wd.system.basic.domain.vo.RoleDetailVO;
import com.wd.system.basic.mapper.RoleMapper;
import com.wd.system.basic.mapper.MenuMapper;
import com.wd.system.basic.mapper.UserMapper;
import com.wd.system.basic.mapper.RolePermissionRelevanceMapper;
import com.wd.system.basic.service.RoleService;
import com.wd.system.basic.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.util.*;

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
        vo.setFieldCodes(permissions.stream()
                .filter(p -> RolePermissionTypeEnum.FIELD.getCode().equals(p.getType()))
                .map(RolePermissionRelevance::getRelevanceValue)
                .filter(StringUtils::isNotBlank)
                .distinct().toList());
        vo.setDataUserIds(permissions.stream()
                .filter(p -> RolePermissionTypeEnum.DATA_USER.getCode().equals(p.getType()))
                .map(RolePermissionRelevance::getRelevanceId)
                .filter(Objects::nonNull)
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
        if (dto.getAuthorized()) {
            flags.add(dto.getPermissionFlag());
        } else {
            flags.remove(dto.getPermissionFlag());
        }
        List<String> permissionFlags = new ArrayList<>(flags);
        if (Objects.equals(role.getPermissionFlag(), permissionFlags)) {
            return true;
        }

        // 仅更新权限标记及自动填充的审计字段。
        Role update = new Role();
        update.setId(role.getId());
        update.setPermissionFlag(permissionFlags);
        AssertUtil.isTrue(baseMapper.updateById(update) > 0, "角色授权失败");
        clearPermissionCacheAfterCommit();
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean authorize(RoleAuthorizeDTO dto) {
        RolePermissionTypeEnum type = CodeDescEnumUtil.getEnumByCode(RolePermissionTypeEnum.class, dto.getType());
        AssertUtil.notNull(type, "授权类型不正确");
        Set<Long> ids = new LinkedHashSet<>(Optional.ofNullable(dto.getRelevanceIds()).orElse(List.of()));
        Set<String> values = new LinkedHashSet<>(Optional.ofNullable(dto.getRelevanceValues()).orElse(List.of()));
        AssertUtil.isTrue(ids.isEmpty() || values.isEmpty(), "关联ID和关联值不能同时有值");
        if (type == RolePermissionTypeEnum.FIELD) {
            AssertUtil.isTrue(ids.isEmpty(), "字段授权只能使用关联值");
            for (String code : values) {
                AssertUtil.isTrue(StringUtils.isNotBlank(code)
                        && CodeDescEnumUtil.isExist(FieldPermissionEnum.class, code), "字段权限编码不存在");
            }
        } else {
            AssertUtil.isTrue(values.isEmpty(), "菜单和用户数据授权只能使用关联ID");
            AssertUtil.isTrue(ids.stream().allMatch(id -> id != null && id > 0), "关联ID必须为正数");
        }

        lockRole(dto.getRoleId());
        validatePermissionIds(type, ids);

        rolePermissionRelevanceMapper.delete(Wrappers.<RolePermissionRelevance>lambdaQuery()
                .eq(RolePermissionRelevance::getRoleId, dto.getRoleId())
                .eq(RolePermissionRelevance::getType, type.getCode()));

        List<RolePermissionRelevance> permissions = new ArrayList<>(ids.size() + values.size());
        for (Long relevanceId : ids) {
            permissions.add(newPermission(dto.getRoleId(), type, relevanceId, ""));
        }
        for (String relevanceValue : values) {
            permissions.add(newPermission(dto.getRoleId(), type, 0L, relevanceValue));
        }
        for (List<RolePermissionRelevance> batch : CollUtil.split(permissions, SuperMapper.DEFAULT_BATCH_SIZE)) {
            AssertUtil.isTrue(rolePermissionRelevanceMapper.insertBatchSomeColumn(batch) == batch.size(), "保存角色授权失败");
        }

        clearPermissionCacheAfterCommit();
        return true;
    }

    private Role lockRole(Long id) {
        AssertUtil.isTrue(id != null && id > 0, "角色ID必须为正数");
        Role role = baseMapper.selectOne(Wrappers.<Role>lambdaQuery()
                .eq(Role::getId, id)
                .last("FOR UPDATE"));
        AssertUtil.notNull(role, "角色不存在或已删除");
        return role;
    }

    private void validatePermissionIds(RolePermissionTypeEnum type, Set<Long> ids) {
        for (List<Long> batch : CollUtil.split(new ArrayList<>(ids), SuperMapper.DEFAULT_BATCH_SIZE)) {
            if (type == RolePermissionTypeEnum.MENU) {
                long count = menuMapper.selectCount(Wrappers.<Menu>lambdaQuery().in(Menu::getId, batch));
                AssertUtil.isTrue(count == batch.size(), "菜单不存在");
            } else if (type == RolePermissionTypeEnum.DATA_USER) {
                long count = userMapper.selectCount(Wrappers.<User>lambdaQuery().in(User::getId, batch));
                AssertUtil.isTrue(count == batch.size(), "用户不存在或已删除");
            }
        }
    }

    private RolePermissionRelevance newPermission(Long roleId, RolePermissionTypeEnum type,
                                                 Long relevanceId, String relevanceValue) {
        RolePermissionRelevance permission = new RolePermissionRelevance();
        permission.setId(IdWorker.getId());
        permission.setRoleId(roleId);
        permission.setType(type.getCode());
        permission.setRelevanceId(relevanceId);
        permission.setRelevanceValue(relevanceValue);
        return permission;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long addRole(RoleAddDTO roleAddParam) {
        log.info("新增角色,参数:{}", JSON.toJSONString(roleAddParam));

        checkUnique(roleAddParam.getCode(), roleAddParam.getName(), null);

        Role role = new Role();

        role.setId(IdWorker.getId());
        role.setCode(roleAddParam.getCode());
        role.setName(roleAddParam.getName());
        role.setStatus(RoleStatusEnum.ENABLED.getCode());
        role.setType(RoleTypeEnum.NORMAL.getCode());
        role.setRemark(Optional.ofNullable(roleAddParam.getRemark()).orElse(""));
        role.setPermissionFlag(null);
        role.setDeleteFlag(CommonConstant.NOT_DELETE_FLAG);

        AssertUtil.isTrue(baseMapper.insert(role) > 0, "新增角色失败");

        clearPermissionCacheAfterCommit();

        return role.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRole(RoleUpdateDTO roleUpdateParam) {
        log.info("更新角色,参数:{}", JSON.toJSONString(roleUpdateParam));

        Role role = getRoleByIdAssertExist(roleUpdateParam.getId());
        checkUnique(roleUpdateParam.getCode(), roleUpdateParam.getName(), role.getId());

        baseMapper.update(Wrappers.<Role>lambdaUpdate()
                .set(Role::getCode, roleUpdateParam.getCode())
                .set(Role::getName, roleUpdateParam.getName())
                .set(Role::getRemark,Optional.ofNullable(roleUpdateParam.getRemark()).orElse(""))
                .eq(Role::getId, role.getId()));

        clearPermissionCacheAfterCommit();

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateStatus(RoleUpdateStatusDTO updateStatusDTO) {
        log.info("更新角色状态,参数:{}", JSON.toJSONString(updateStatusDTO));

        Role role = getRoleByIdAssertExist(updateStatusDTO.getId());
        String status = updateStatusDTO.getStatus();
        if (Objects.equals(role.getStatus(), status)) {
            return true;
        }

        boolean success = baseMapper.update(Wrappers.<Role>lambdaUpdate()
                .set(Role::getStatus, status)
                .eq(Role::getId, role.getId())) > 0;
        if (success) {
            clearPermissionCacheAfterCommit();
        }
        return success;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteRoleById(IdDTO idParam) {
        log.info("删除角色,参数:{}", JSON.toJSONString(idParam));
        Long id = idParam.getId();

        // 判断数据是否存在
        Role role = getRoleByIdAssertExist(id);

        // 逻辑删除使用角色ID作为删除标记，允许重新使用已删除角色的编码和名称。
        boolean success = baseMapper.deleteById(role) > 0;
        if (success) {
            clearPermissionCacheAfterCommit();
        }

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
}
