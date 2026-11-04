package com.wd.system.basic.convert;

import com.wd.system.basic.domain.entity.Menu;
import com.wd.system.basic.domain.vo.MenuDetailVO;
import com.wd.system.basic.domain.vo.MenuTreeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface MenuConverter {

    @Mapping(target = "menuTypeDesc", ignore = true)
    @Mapping(target = "createUserName", ignore = true)
    @Mapping(target = "updateUserName", ignore = true)
    MenuDetailVO convertMenuDetailVO(Menu menu);

    @Mapping(target = "menuTypeDesc", ignore = true)
    @Mapping(target = "children", ignore = true)
    MenuTreeVO convertMenuTreeVO(Menu menu);
}
