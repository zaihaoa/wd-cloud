package com.wd.system.basic.service;

import com.wd.common.core.model.PageInfo;
import com.wd.common.mybatisplus.SuperService;
import com.wd.system.basic.domain.dto.CommonLogAddDTO;
import com.wd.system.basic.domain.dto.CommonLogPageParamDTO;
import com.wd.system.basic.domain.entity.CommonLog;
import com.wd.system.basic.domain.vo.CommonLogPageVO;

import java.util.List;

/**
 * <p>
 * 通用日志 服务类
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
public interface CommonLogService extends SuperService<CommonLog> {

    /**
     * 列表分页查询
     *
     * @param pageParam 查询条件
     * @return 列表数据
     */
    PageInfo<CommonLogPageVO> pageList(CommonLogPageParamDTO pageParam);

    /**
     * 添加日志
     */
    boolean addLog(CommonLogAddDTO commonLogAddParam);

    /**
     * 批量添加日志
     */
    boolean batchAddLog(List<CommonLogAddDTO> commonLogAddParams);

}
