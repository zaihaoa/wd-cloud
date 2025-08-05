package com.wd.system.basic.convert;

import org.mapstruct.Mapper;
import com.wd.system.basic.domain.dto.RolePageDTO;
import com.wd.system.basic.domain.vo.RolePageVO;


@Mapper(componentModel = "spring")
public interface RoleConverter {

    RolePageVO convertRolePageVO(RolePageDTO dbRecord);

}
