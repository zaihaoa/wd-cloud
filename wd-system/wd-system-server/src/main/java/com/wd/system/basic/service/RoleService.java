package com.wd.system.basic.service;

import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.IdDTO;
import com.wd.system.basic.domain.dto.RolePageParamDTO;
import com.wd.system.basic.domain.dto.RoleAuthorizeAllDTO;
import com.wd.system.basic.domain.dto.RoleAuthorizeDTO;
import com.wd.system.basic.domain.vo.RoleDetailVO;
import com.wd.system.basic.domain.vo.RolePageVO;
import com.wd.system.basic.domain.dto.RoleAddDTO;
import com.wd.system.basic.domain.dto.RoleUpdateDTO;
import com.wd.system.basic.domain.dto.RoleUpdateStatusDTO;
import com.wd.system.basic.domain.entity.Role;
import com.wd.common.mybatisplus.SuperService;

import java.io.Serializable;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
public interface RoleService extends SuperService<Role> {

    /**
     * 列表分页查询
     *
     * @param pageParam 查询条件
     * @return 列表数据
     */
    PageInfo<RolePageVO> pageList(RolePageParamDTO pageParam);

    /**
     * 添加角色，自动生成ID，删除标记初始化为0
     *
     * @return 添加成功数据ID
     */
    long addRole(RoleAddDTO roleAddParam);

    /**
     * 更新未删除角色，权限标记支持传空列表或null清空
     *
     * @return 是否更新成功
     */
    boolean updateRole(RoleUpdateDTO roleUpdateParam);

    /**
     * 启用或禁用未删除的角色。
     */
    boolean updateStatus(RoleUpdateStatusDTO updateStatusDTO);

    /**
     * 逻辑删除角色，删除标记更新为角色ID
     *
     * @param id 数据ID
     * @return 是否删除成功
     */
    boolean deleteRoleById(IdDTO id);

    /**
     * 根据ID查询未删除角色，包含权限标记
     * @param id 数据ID
     * @return 数据
     */
    Role getRoleById(Serializable id);

    /**
     * 根据ID查询未删除角色，并断言数据存在
     * @param id 数据ID
     * @return 数据
     */
    Role getRoleByIdAssertExist(Serializable id);

    /**
     * 获取未删除角色详情及已保存的三类具体授权。
     */
    RoleDetailVO detail(Long id);

    /**
     * 添加或移除全部权限标记，保留其他标记及具体授权。
     */
    boolean authorizeAll(RoleAuthorizeAllDTO dto);

    /**
     * 覆盖保存当前授权类型，空集合清空，保留其他类型及全部权限标记。
     */
    boolean authorize(RoleAuthorizeDTO dto);
}
