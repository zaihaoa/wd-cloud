package com.wd.system.basic.mapper;

import com.wd.system.basic.domain.dto.GlobalConfigPageParamDTO;
import com.wd.system.basic.domain.dto.GlobalConfigPageDTO;
import com.wd.system.basic.domain.entity.GlobalConfig;
import com.wd.common.mybatisplus.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * <p>
 * 全局配置 Mapper 接口
 * </p>
 *
 * @author huangwenda
 * @since 2025-10-11
 */
@Mapper
public interface GlobalConfigMapper extends SuperMapper<GlobalConfig> {

    int pageCount(@Param("param") GlobalConfigPageParamDTO pageParam);

    List<GlobalConfigPageDTO> pageList(@Param("param") GlobalConfigPageParamDTO pageParam);

}
