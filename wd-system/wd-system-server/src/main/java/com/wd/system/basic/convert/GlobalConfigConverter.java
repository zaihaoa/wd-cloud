package com.wd.system.basic.convert;

import com.wd.system.basic.domain.vo.GlobalConfigDetailVO;
import org.mapstruct.Mapper;
import com.wd.system.basic.domain.dto.GlobalConfigPageDTO;
import com.wd.system.basic.domain.vo.GlobalConfigPageVO;
import com.wd.system.basic.domain.entity.GlobalConfig;


@Mapper(componentModel = "spring")
public interface GlobalConfigConverter {

    GlobalConfigPageVO convertGlobalConfigPageVO(GlobalConfigPageDTO dbRecord);

    GlobalConfigDetailVO convertGlobalConfigDetailVO(GlobalConfig record);

}
