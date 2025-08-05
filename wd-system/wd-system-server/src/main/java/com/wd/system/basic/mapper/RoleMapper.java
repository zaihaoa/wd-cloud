package com.wd.system.basic.mapper;

import com.wd.system.basic.domain.dto.RolePageParamDTO;
import com.wd.system.basic.domain.dto.RolePageDTO;
import com.wd.system.basic.domain.entity.Role;
import com.wd.common.mybatisplus.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * <p>
 * 角色 Mapper 接口
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Mapper
public interface RoleMapper extends SuperMapper<Role> {

    int pageCount(@Param("param") RolePageParamDTO pageParam);

    List<RolePageDTO> pageList(@Param("param") RolePageParamDTO pageParam);

}
