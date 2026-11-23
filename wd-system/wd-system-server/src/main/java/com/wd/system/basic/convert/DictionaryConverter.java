package com.wd.system.basic.convert;

import com.wd.system.basic.domain.dto.DictionaryItemPageDTO;
import com.wd.system.basic.domain.dto.DictionaryPageDTO;
import com.wd.system.basic.domain.entity.Dictionary;
import com.wd.system.basic.domain.entity.DictionaryItem;
import com.wd.system.basic.domain.vo.DictionaryDetailVO;
import com.wd.system.basic.domain.vo.DictionaryItemDetailVO;
import com.wd.system.basic.domain.vo.DictionaryItemPageVO;
import com.wd.system.basic.domain.vo.DictionaryPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DictionaryConverter {

    DictionaryPageVO toDictionaryPageVO(DictionaryPageDTO record);

    DictionaryDetailVO toDictionaryDetailVO(Dictionary record);

    DictionaryItemPageVO toDictionaryItemPageVO(DictionaryItemPageDTO record);

    DictionaryItemDetailVO toDictionaryItemDetailVO(DictionaryItem record);
}
