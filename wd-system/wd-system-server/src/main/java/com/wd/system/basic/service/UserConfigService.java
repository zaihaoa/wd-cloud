package com.wd.system.basic.service;

import com.wd.common.mybatisplus.SuperService;
import com.wd.system.basic.domain.dto.UserConfigAddUpdateDTO;
import com.wd.system.basic.domain.dto.UserConfigQueryDTO;
import com.wd.system.basic.domain.entity.UserConfig;
import com.wd.system.basic.domain.vo.UserConfigDetailVO;

/**
 * <p>
 * 用户配置 服务类
 * </p>
 *
 * @author huangwenda
 * @since 2026-11-09
 */
public interface UserConfigService extends SuperService<UserConfig> {

    /**
     * 新增或更新
     */
    UserConfigDetailVO addOrUpdate(UserConfigAddUpdateDTO addUpdateDTO);

    /**
     * 详情
     */
    UserConfigDetailVO detail(UserConfigQueryDTO query);

    /**
     * 删除
     */
    boolean delete(UserConfigQueryDTO query);
}
