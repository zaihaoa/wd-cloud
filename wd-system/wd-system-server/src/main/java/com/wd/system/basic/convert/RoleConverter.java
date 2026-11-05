package com.wd.system.basic.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.wd.system.basic.domain.entity.Role;
import com.wd.system.basic.domain.vo.RoleDetailVO;
import com.wd.system.basic.domain.dto.RolePageDTO;
import com.wd.system.basic.domain.vo.RolePageVO;


@Mapper(componentModel = "spring")
public interface RoleConverter {

    RolePageVO convertRolePageVO(RolePageDTO dbRecord);

    @Mapping(target = "menuIds", ignore = true)
    @Mapping(target = "fieldCodes", ignore = true)
    @Mapping(target = "dataUserIds", ignore = true)
    RoleDetailVO convertRoleDetailVO(Role role);

}
