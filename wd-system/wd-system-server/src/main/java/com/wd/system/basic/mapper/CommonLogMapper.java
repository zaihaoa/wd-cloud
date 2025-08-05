package com.wd.system.basic.mapper;

import com.wd.common.mybatisplus.SuperMapper;
import com.wd.system.basic.domain.dto.CommonLogPageDTO;
import com.wd.system.basic.domain.dto.CommonLogPageParamDTO;
import com.wd.system.basic.domain.entity.CommonLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 通用日志 Mapper 接口
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Mapper
public interface CommonLogMapper extends SuperMapper<CommonLog> {

    int pageCount(@Param("param") CommonLogPageParamDTO pageParam);

    List<CommonLogPageDTO> pageList(@Param("param") CommonLogPageParamDTO pageParam);

}
