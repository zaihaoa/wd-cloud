package com.wd.system.basic.service;

import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.SelectCombobox;
import com.wd.common.mybatisplus.SuperService;
import com.wd.system.basic.domain.dto.*;
import com.wd.system.basic.domain.entity.User;
import com.wd.system.basic.domain.vo.UserDetailVO;
import com.wd.system.basic.domain.vo.UserInfoVO;
import com.wd.system.basic.domain.vo.UserPageVO;
import jakarta.validation.Valid;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
public interface UserService extends SuperService<User> {

    /**
     * 列表分页查询
     *
     * @param pageParam 查询条件
     * @return 列表数据
     */
    PageInfo<UserPageVO> pageList(UserPageParamDTO pageParam);

    /**
     * 添加
     *
     * @return 添加成功数据ID
     */
    long addUser(UserAddDTO userAddParam);

    /**
     * 更新
     *
     * @return 是否更新成功
     */
    boolean updateUser(UserUpdateDTO userUpdateParam);

    /**
     * 删除
     *
     * @param id 数据ID
     * @return 是否删除成功
     */
    boolean deleteUserById(IdDTO id);

    /**
     * 根据ID查询
     * @param id 数据ID
     * @return 数据
     */
    User getUserById(Serializable id);

    /**
     * 根据ID查询，并断言数据存在
     * @param id 数据ID
     * @return 数据
     */
    User getUserByIdAssertExist(Serializable id);

    /**
     * 获取用户ID和姓名称映射
     *
     * @return map<用户ID, 姓名>
     */
    Map<Long, String> getUserIdNameMap(Collection<Long> userIds);

    /**
     * 获取用户ID和姓名称映射
     *
     * @return map<用户ID, 姓名>
     */
    Map<Long, String> getUserIdNameMap();

    /**
     * 根据用户名查询
     */
    User getByUsername(String username);

    /**
     * 更新状态
     */
    boolean updateStatus(UserUpdateStatusDTO updateStatusDTO);

    /**
     * 修改密码
     */
    boolean updatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO);

    /**
     * 重置密码
     */
    boolean resetPassword(UserResetPasswordDTO userResetPasswordDTO);

    /**
     * 用户详情
     */
    UserDetailVO detail(IdDTO idDTO);

    /**
     * 当前登录用户信息
     */
    UserInfoVO currentUserInfo();

    /**
     * 用户下拉框
     */
    List<SelectCombobox> userCombobox(String status);
}
