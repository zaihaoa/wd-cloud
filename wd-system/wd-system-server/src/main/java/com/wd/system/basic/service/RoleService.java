package com.wd.system.basic.service;

import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.Id;
import com.wd.system.basic.domain.dto.RolePageParamDTO;
import com.wd.system.basic.domain.vo.RolePageVO;
import com.wd.system.basic.domain.dto.RoleAddDTO;
import com.wd.system.basic.domain.dto.RoleUpdateDTO;
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
     * 添加
     *
     * @return 添加成功数据ID
     */
    long addRole(RoleAddDTO roleAddParam);

    /**
     * 更新
     *
     * @return 是否更新成功
     */
    boolean updateRole(RoleUpdateDTO roleUpdateParam);

    /**
     * 删除
     *
     * @param id 数据ID
     * @return 是否删除成功
     */
    boolean deleteRoleById(Id id);

    /**
     * 根据ID查询
     * @param id 数据ID
     * @return 数据
     */
    Role getRoleById(Serializable id);

    /**
     * 根据ID查询，并断言数据存在
     * @param id 数据ID
     * @return 数据
     */
    Role getRoleByIdAssertExist(Serializable id);
}
