package com.wd.system.basic.service;

import com.wd.common.core.model.Id;
import com.wd.common.mybatisplus.SuperService;
import com.wd.system.basic.domain.dto.MenuAddDTO;
import com.wd.system.basic.domain.dto.MenuUpdateDTO;
import com.wd.system.basic.domain.entity.Menu;
import com.wd.system.basic.domain.vo.MenuDetailVO;
import com.wd.system.basic.domain.vo.MenuTreeVO;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
public interface MenuService extends SuperService<Menu> {

    /**
     * 添加
     *
     * @return 添加成功数据ID
     */
    long addMenu(MenuAddDTO menuAddDTO);

    /**
     * 更新
     *
     * @param id 主键ID
     * @return 是否更新成功
     */
    boolean updateMenu(MenuUpdateDTO menuUpdateDTO);

    /**
     * 删除
     *
     * @param idParam id
     * @return 是否删除成功
     */
    boolean deleteMenuById(Id idParam);

    /**
     * 所有菜单列表Tree（过滤个人用户权限）
     *
     * @return 过滤个人权限后的菜单列表Tree
     */
    List<MenuTreeVO> userPermissionMenuTree();

    /**
     * 所有菜单列表Tree
     *
     * @return 菜单列表Tree
     */
    List<MenuTreeVO> menuTree();

    /**
     * 所有菜单列表
     */
    List<Menu> menuList();

    /**
     * 所有菜单ID
     */
    List<Long> allMenuIds();

    /**
     * 获取详情
     */
    MenuDetailVO detail(Id idParam);

    /**
     * 所有菜单对应的权限
     */
    List<String> getPermissionByIds(Collection<Long> menuIds);
}
