package com.wd.system.basic.convert;

import org.mapstruct.Mapper;
import com.wd.system.basic.domain.dto.UserPageDTO;
import com.wd.system.basic.domain.vo.UserPageVO;


@Mapper(componentModel = "spring")
public interface UserConverter {

    UserPageVO convertUserPageVO(UserPageDTO dbRecord);

}
