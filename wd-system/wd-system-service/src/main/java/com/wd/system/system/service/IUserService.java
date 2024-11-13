package com.wd.system.system.service;


import com.wd.common.core.PageInfo;
import com.wd.common.core.R;
import com.wd.common.mybatisplus.SuperService;
import com.wd.system.system.dto.UserAddDTO;
import com.wd.system.system.dto.UserPageParam;
import com.wd.system.system.dto.UserUpdateDTO;
import com.wd.system.system.entity.User;
import com.wd.system.system.vo.UserPageVO;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author huangwenda
 * @since 2023-01-30
 */
public interface IUserService extends SuperService<User> {

    /**
     * 列表分页查询
     *
     * @param pageParam 查询条件
     * @return 列表数据
     */
    PageInfo<UserPageVO> page(UserPageParam pageParam);

    /**
     * 列表分页查询
     *
     * @param pageParam 查询条件
     * @return 下载任务ID
     */
    long exportPage(UserPageParam pageParam);

    /**
     * 添加用户
     *
     * @param addUserParam 添加用户参数
     * @return 是否添加成功
     */
    boolean add(UserAddDTO addUserParam);

    /**
     * 更新用户
     *
     * @param id         主键ID
     * @param updateUserParam 更新用户参数
     * @return 是否更新成功
     */
    boolean update(Long id, UserUpdateDTO updateUserParam);

    /**
     * 删除用户
     *
     * @param id id
     * @return 是否删除成功
     */
    boolean deleteById(Long id);


    /**
     * 获取显示名称
     * @param userId 用户id
     * @return 显示名称
     */
    String getShowName(Long userId);
}
