package com.wd.system.basic.convert;

import com.wd.system.basic.domain.entity.Menu;
import com.wd.system.basic.domain.vo.MenuDetailVO;
import com.wd.system.basic.domain.vo.MenuTreeVO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MenuConverter {

    MenuDetailVO convertMenuDetailVO(Menu menu);

    MenuTreeVO convertMenuTreeVO(Menu menu);
}
