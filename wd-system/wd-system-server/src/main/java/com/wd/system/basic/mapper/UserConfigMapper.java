package com.wd.system.basic.mapper;

import com.wd.common.mybatisplus.SuperMapper;
import com.wd.system.basic.domain.entity.UserConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户配置 Mapper 接口
 * </p>
 *
 * @author huangwenda
 * @since 2026-11-09
 */
@Mapper
public interface UserConfigMapper extends SuperMapper<UserConfig> {

}
