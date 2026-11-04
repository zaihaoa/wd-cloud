package com.wd.system.basic.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.core.util.I18nUtil;
import com.wd.common.mybatisplus.PageUtil;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.RoleConverter;
import com.wd.system.basic.domain.dto.RoleAddDTO;
import com.wd.system.basic.domain.dto.RolePageDTO;
import com.wd.system.basic.domain.dto.RolePageParamDTO;
import com.wd.system.basic.domain.dto.RoleUpdateDTO;
import com.wd.system.basic.domain.entity.Role;
import com.wd.system.basic.domain.enums.RoleStatusEnum;
import com.wd.system.basic.domain.enums.RoleTypeEnum;
import com.wd.system.basic.domain.vo.RolePageVO;
import com.wd.system.basic.mapper.RoleMapper;
import com.wd.system.basic.service.RoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

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
        return dbRecords
                .stream()
                .map(roleConverter::convertRolePageVO)
                .toList();
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
        role.setStatus(Optional.ofNullable(roleAddParam.getStatus()).orElse(RoleStatusEnum.ENABLED.getCode()));
        role.setType(Optional.ofNullable(roleAddParam.getType()).orElse(RoleTypeEnum.NORMAL.getCode()));
        role.setRemark(Optional.ofNullable(roleAddParam.getRemark()).orElse(""));
        role.setPermissionFlag(roleAddParam.getPermissionFlag());
        role.setDeleteFlag(CommonConstant.NOT_DELETE_FLAG);

        AssertUtil.isTrue(baseMapper.insert(role) > 0, "新增角色失败");
        clearPermissionCache();

        return role.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRole(RoleUpdateDTO roleUpdateParam) {
        log.info("更新角色,参数:{}", JSON.toJSONString(roleUpdateParam));

        Role role = getRoleByIdAssertExist(roleUpdateParam.getId());
        checkUnique(roleUpdateParam.getCode(), roleUpdateParam.getName(), role.getId());

        role.setCode(roleUpdateParam.getCode());
        role.setName(roleUpdateParam.getName());
        role.setStatus(Optional.ofNullable(roleUpdateParam.getStatus()).orElse(RoleStatusEnum.ENABLED.getCode()));
        role.setType(Optional.ofNullable(roleUpdateParam.getType()).orElse(RoleTypeEnum.NORMAL.getCode()));
        role.setRemark(Optional.ofNullable(roleUpdateParam.getRemark()).orElse(""));
        role.setPermissionFlag(roleUpdateParam.getPermissionFlag());

        boolean success = baseMapper.updateById(role) > 0;
        if (success) {
            clearPermissionCache();
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
            clearPermissionCache();
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
}
