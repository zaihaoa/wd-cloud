package com.wd.system.mapper.system;

import com.wd.common.mybatisplus.SuperMapper;
import com.wd.system.system.dto.UserPageDTO;
import com.wd.system.system.dto.UserPageParam;
import com.wd.system.system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户 Mapper 接口
 * </p>
 *
 * @author huangwenda
 * @since 2023-01-30
 */
@Mapper
public interface UserMapper extends SuperMapper<User> {

    long pageTotal(@Param("param") UserPageParam pageParam);

    List<UserPageDTO> page(@Param("param") UserPageParam pageParam);
}
