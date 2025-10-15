package com.wd.system.basic.service;

import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.Id;
import com.wd.system.basic.domain.dto.GlobalConfigPageParamDTO;
import com.wd.system.basic.domain.vo.GlobalConfigPageVO;
import com.wd.system.basic.domain.vo.GlobalConfigDetailVO;
import com.wd.system.basic.domain.dto.GlobalConfigAddDTO;
import com.wd.system.basic.domain.dto.GlobalConfigUpdateDTO;
import com.wd.system.basic.domain.entity.GlobalConfig;
import com.wd.common.mybatisplus.SuperService;

/**
 * <p>
 * 全局配置 服务类
 * </p>
 *
 * @author huangwenda
 * @since 2025-10-11
 */
public interface GlobalConfigService extends SuperService<GlobalConfig> {

    /**
     * 列表分页查询
     *
     * @param pageParam 查询条件
     * @return 列表数据
     */
    PageInfo<GlobalConfigPageVO> pageList(GlobalConfigPageParamDTO pageParam);

    /**
     * 添加
     *
     * @return 添加成功数据ID
     */
    long addGlobalConfig(GlobalConfigAddDTO globalConfigAddParam);

    /**
     * 更新
     *
     * @return 是否更新成功
     */
    boolean updateGlobalConfig(GlobalConfigUpdateDTO globalConfigUpdateParam);

    /**
     * 获取详情
     *
     * @return 详情
     */
    GlobalConfigDetailVO detail(Long id);

    /**
     * 删除
     *
     * @param id 数据ID
     * @return 是否删除成功
     */
    boolean deleteGlobalConfigById(Id id);

    /**
     * 根据ID查询
     * @param id 数据ID
     * @return 数据
     */
    GlobalConfig getGlobalConfigById(Long id);

    /**
     * 根据ID查询，并断言数据存在
     * @param id 数据ID
     * @return 数据
     */
    GlobalConfig getGlobalConfigByIdAssertExist(Long id);
}
