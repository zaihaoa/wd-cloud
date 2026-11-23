package com.wd.system.basic.service;

import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.IdsDTO;
import com.wd.common.core.model.PageInfo;
import com.wd.common.mybatisplus.SuperService;
import com.wd.system.basic.domain.dto.DictionaryAddDTO;
import com.wd.system.basic.domain.dto.DictionaryBatchUpdateStatusDTO;
import com.wd.system.basic.domain.dto.DictionaryItemAddDTO;
import com.wd.system.basic.domain.dto.DictionaryItemPageParamDTO;
import com.wd.system.basic.domain.dto.DictionaryItemUpdateDTO;
import com.wd.system.basic.domain.dto.DictionaryPageParamDTO;
import com.wd.system.basic.domain.dto.DictionaryUpdateDTO;
import com.wd.system.basic.domain.entity.Dictionary;
import com.wd.system.basic.domain.vo.DictionaryDetailVO;
import com.wd.system.basic.domain.vo.DictionaryItemDetailVO;
import com.wd.system.basic.domain.vo.DictionaryItemPageVO;
import com.wd.system.basic.domain.vo.DictionaryPageVO;

public interface DictionaryService extends SuperService<Dictionary> {

    /**
     * 分页查询未删除的字典。
     *
     * @param pageParam 筛选条件和分页参数
     * @return 字典分页结果
     */
    PageInfo<DictionaryPageVO> pageList(DictionaryPageParamDTO pageParam);

    /**
     * 新增字典，编码须在未删除的字典中唯一。
     *
     * @param param 字典新增参数
     * @return 新建字典的主键 ID
     */
    long addDictionary(DictionaryAddDTO param);

    /**
     * 覆盖更新字典的描述、状态和备注。描述和状态必填，备注未传或为 {@code null} 时写入空字符串。
     *
     * @param param 字典 ID 和待更新的字段
     * @return 更新成功或字段值无变化时返回 {@code true}
     */
    boolean updateDictionary(DictionaryUpdateDTO param);

    /**
     * 批量启用或禁用未删除的字典。任一 ID 不存在或已删除时整批失败。
     *
     * @param param 待更新的字典 ID 集合和目标状态
     * @return 整批更新成功或全部已是目标状态时返回 {@code true}
     */
    boolean batchUpdateDictionaryStatus(DictionaryBatchUpdateStatusDTO param);

    /**
     * 逻辑删除字典。字典下仍有未删除的明细时拒绝删除。
     *
     * @param param 待删除的字典 ID
     * @return 删除成功时返回 {@code true}
     */
    boolean deleteDictionaryById(IdDTO param);

    /**
     * 按主键查询未删除的字典。
     *
     * @param id 字典主键 ID
     * @return 字典详情，查无记录时返回 {@code null}
     */
    DictionaryDetailVO detail(Long id);

    /**
     * 分页查询未删除的字典明细，按所属字典编码和状态精确筛选，按明细编码、描述和备注模糊筛选。
     *
     * @param pageParam 筛选条件和分页参数
     * @return 字典明细分页结果
     */
    PageInfo<DictionaryItemPageVO> itemPageList(DictionaryItemPageParamDTO pageParam);

    /**
     * 在指定字典下新增明细，明细编码须在该字典的未删除明细中唯一。
     *
     * @param param 所属字典编码和明细新增参数
     * @return 新建明细的主键 ID
     */
    long addDictionaryItem(DictionaryItemAddDTO param);

    /**
     * 覆盖更新字典明细。明细编码和状态必填，描述和备注未传或为 {@code null} 时写入空字符串，
     * 显示顺序未传或为 {@code null} 时写入 0。
     *
     * @param param 明细 ID 和待更新的字段
     * @return 更新成功或字段值无变化时返回 {@code true}
     */
    boolean updateDictionaryItem(DictionaryItemUpdateDTO param);

    /**
     * 批量启用或禁用未删除的字典明细，支持跨字典操作。任一 ID 不存在或已删除时整批失败。
     *
     * @param param 待更新的明细 ID 集合和目标状态
     * @return 整批更新成功或全部已是目标状态时返回 {@code true}
     */
    boolean batchUpdateDictionaryItemStatus(DictionaryBatchUpdateStatusDTO param);

    /**
     * 批量逻辑删除字典明细并记录操作日志。重复 ID 按一条处理，任一明细不存在或已删除时整批失败。
     *
     * @param param 待删除的明细 ID 集合
     * @return 整批删除成功时返回 {@code true}
     */
    boolean deleteDictionaryItemsByIds(IdsDTO param);

    /**
     * 按主键查询未删除的字典明细。
     *
     * @param id 字典明细主键 ID
     * @return 字典明细详情，查无记录时返回 {@code null}
     */
    DictionaryItemDetailVO itemDetail(Long id);
}
