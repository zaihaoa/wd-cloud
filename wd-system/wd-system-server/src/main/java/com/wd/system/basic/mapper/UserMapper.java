package com.wd.system.basic.mapper;

import com.wd.system.basic.domain.dto.UserPageParamDTO;
import com.wd.system.basic.domain.dto.UserPageDTO;
import com.wd.system.basic.domain.entity.User;
import com.wd.common.mybatisplus.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * <p>
 * 用户 Mapper 接口
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Mapper
public interface UserMapper extends SuperMapper<User> {

    int pageCount(@Param("param") UserPageParamDTO pageParam);

    List<UserPageDTO> pageList(@Param("param") UserPageParamDTO pageParam);

}
