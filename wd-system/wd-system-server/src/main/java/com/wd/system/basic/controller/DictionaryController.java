package com.wd.system.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.wd.common.core.annotions.ApifoxFolder;
import com.wd.common.core.constants.PermissionConstant;
import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.IdsDTO;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.R;
import com.wd.system.basic.domain.dto.DictionaryAddDTO;
import com.wd.system.basic.domain.dto.DictionaryBatchUpdateStatusDTO;
import com.wd.system.basic.domain.dto.DictionaryItemAddDTO;
import com.wd.system.basic.domain.dto.DictionaryItemPageParamDTO;
import com.wd.system.basic.domain.dto.DictionaryItemUpdateDTO;
import com.wd.system.basic.domain.dto.DictionaryPageParamDTO;
import com.wd.system.basic.domain.dto.DictionaryUpdateDTO;
import com.wd.system.basic.domain.vo.DictionaryDetailVO;
import com.wd.system.basic.domain.vo.DictionaryItemDetailVO;
import com.wd.system.basic.domain.vo.DictionaryItemPageVO;
import com.wd.system.basic.domain.vo.DictionaryPageVO;
import com.wd.system.basic.service.DictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@ApifoxFolder("字典")
@RestController
@RequestMapping("/dictionary")
public class DictionaryController {

    @Resource
    private DictionaryService dictionaryService;

    @Operation(summary = "字典分页查询")
    @SaCheckPermission(PermissionConstant.DICTIONARY_LIST)
    @PostMapping("/page")
    public R<PageInfo<DictionaryPageVO>> pageList(@RequestBody @Valid DictionaryPageParamDTO pageParam) {
        return R.success(dictionaryService.pageList(pageParam));
    }

    @Operation(summary = "新增字典")
    @SaCheckPermission(PermissionConstant.DICTIONARY_ADD)
    @PostMapping("/add")
    public R<Long> addDictionary(@RequestBody @Valid DictionaryAddDTO param) {
        return R.success(dictionaryService.addDictionary(param));
    }

    @Operation(summary = "编辑字典")
    @SaCheckPermission(PermissionConstant.DICTIONARY_UPDATE)
    @PostMapping("/update")
    public R<Boolean> updateDictionary(@RequestBody @Valid DictionaryUpdateDTO param) {
        return R.success(dictionaryService.updateDictionary(param));
    }

    @Operation(summary = "批量启用或禁用字典")
    @SaCheckPermission(PermissionConstant.DICTIONARY_UPDATE)
    @PostMapping("/update-status")
    public R<Boolean> batchUpdateDictionaryStatus(@RequestBody @Valid DictionaryBatchUpdateStatusDTO param) {
        return R.success(dictionaryService.batchUpdateDictionaryStatus(param));
    }

    @Operation(summary = "删除字典")
    @SaCheckPermission(PermissionConstant.DICTIONARY_DELETE)
    @PostMapping("/delete")
    public R<Boolean> deleteDictionaryById(@RequestBody @Valid IdDTO param) {
        return R.success(dictionaryService.deleteDictionaryById(param));
    }

    @Operation(summary = "获取字典详情")
    @SaCheckPermission(PermissionConstant.DICTIONARY_LIST)
    @GetMapping("/detail")
    public R<DictionaryDetailVO> detail(@RequestParam("id") Long id) {
        return R.success(dictionaryService.detail(id));
    }

    @Operation(summary = "字典明细分页查询")
    @SaCheckPermission(PermissionConstant.DICTIONARY_LIST)
    @PostMapping("/item-page")
    public R<PageInfo<DictionaryItemPageVO>> itemPageList(@RequestBody @Valid DictionaryItemPageParamDTO pageParam) {
        return R.success(dictionaryService.itemPageList(pageParam));
    }

    @Operation(summary = "新增字典明细")
    @SaCheckPermission(PermissionConstant.DICTIONARY_ADD)
    @PostMapping("/item-add")
    public R<Long> addDictionaryItem(@RequestBody @Valid DictionaryItemAddDTO param) {
        return R.success(dictionaryService.addDictionaryItem(param));
    }

    @Operation(summary = "编辑字典明细")
    @SaCheckPermission(PermissionConstant.DICTIONARY_UPDATE)
    @PostMapping("/item-update")
    public R<Boolean> updateDictionaryItem(@RequestBody @Valid DictionaryItemUpdateDTO param) {
        return R.success(dictionaryService.updateDictionaryItem(param));
    }

    @Operation(summary = "批量启用或禁用字典明细")
    @SaCheckPermission(PermissionConstant.DICTIONARY_UPDATE)
    @PostMapping("/item-update-status")
    public R<Boolean> batchUpdateDictionaryItemStatus(@RequestBody @Valid DictionaryBatchUpdateStatusDTO param) {
        return R.success(dictionaryService.batchUpdateDictionaryItemStatus(param));
    }

    @Operation(summary = "批量删除字典明细")
    @SaCheckPermission(PermissionConstant.DICTIONARY_DELETE)
    @PostMapping("/item-delete")
    public R<Boolean> deleteDictionaryItemsByIds(@RequestBody @Valid IdsDTO param) {
        return R.success(dictionaryService.deleteDictionaryItemsByIds(param));
    }

    @Operation(summary = "获取字典明细详情")
    @SaCheckPermission(PermissionConstant.DICTIONARY_LIST)
    @GetMapping("/item-detail")
    public R<DictionaryItemDetailVO> itemDetail(@RequestParam("id") Long id) {
        return R.success(dictionaryService.itemDetail(id));
    }
}
