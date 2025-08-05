package com.wd.system.basic.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wd.common.core.model.Id;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.core.util.I18nUtil;
import com.wd.common.mybatisplus.PageUtil;
import com.wd.system.basic.convert.RoleConverter;
import com.wd.system.basic.domain.dto.RoleAddDTO;
import com.wd.system.basic.domain.dto.RolePageDTO;
import com.wd.system.basic.domain.dto.RolePageParamDTO;
import com.wd.system.basic.domain.dto.RoleUpdateDTO;
import com.wd.system.basic.domain.entity.Role;
import com.wd.system.basic.domain.vo.RolePageVO;
import com.wd.system.basic.mapper.RoleMapper;
import com.wd.system.basic.service.RoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

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
                .map(dbRecord -> {
                    RolePageVO vo = roleConverter.convertRolePageVO(dbRecord);


                    return vo;
                })
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long addRole(RoleAddDTO roleAddParam) {
        log.info("新增角色,参数:{}", JSON.toJSONString(roleAddParam));

        Role role = new Role();

        role.setId(roleAddParam.getId());
        role.setName(roleAddParam.getName());
        role.setStatus(roleAddParam.getStatus());
        role.setType(roleAddParam.getType());
        role.setDeleteFlag(roleAddParam.getDeleteFlag());

        return role.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRole(RoleUpdateDTO roleUpdateParam) {
        log.info("更新角色,参数:{}", JSON.toJSONString(roleUpdateParam));
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteRoleById(Id idParam) {
        log.info("删除角色,参数:{}", JSON.toJSONString(idParam));
        Long id = idParam.getId();

        // 判断数据是否存在
        getRoleByIdAssertExist(id);

        boolean success = baseMapper.deleteById(id) > 0;

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
}
