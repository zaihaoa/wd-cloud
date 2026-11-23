package com.wd.system.basic.mapper;

import com.wd.common.mybatisplus.SuperMapper;
import com.wd.system.basic.domain.dto.DictionaryItemPageDTO;
import com.wd.system.basic.domain.dto.DictionaryItemPageParamDTO;
import com.wd.system.basic.domain.entity.DictionaryItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DictionaryItemMapper extends SuperMapper<DictionaryItem> {

    int pageCount(@Param("param") DictionaryItemPageParamDTO pageParam);

    List<DictionaryItemPageDTO> pageList(@Param("param") DictionaryItemPageParamDTO pageParam);
}
