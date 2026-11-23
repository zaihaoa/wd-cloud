package com.wd.system.basic.mapper;

import com.wd.common.mybatisplus.SuperMapper;
import com.wd.system.basic.domain.dto.DictionaryPageDTO;
import com.wd.system.basic.domain.dto.DictionaryPageParamDTO;
import com.wd.system.basic.domain.entity.Dictionary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DictionaryMapper extends SuperMapper<Dictionary> {

    int pageCount(@Param("param") DictionaryPageParamDTO pageParam);

    List<DictionaryPageDTO> pageList(@Param("param") DictionaryPageParamDTO pageParam);
}
