package com.wd.system.basic.convert;

import com.wd.system.basic.domain.entity.User;
import com.wd.system.basic.domain.vo.UserDetailVO;
import com.wd.system.basic.domain.vo.UserInfoVO;
import org.mapstruct.Mapper;
import com.wd.system.basic.domain.dto.UserPageDTO;
import com.wd.system.basic.domain.vo.UserPageVO;


@Mapper(componentModel = "spring")
public interface UserConverter {

    UserPageVO convertUserPageVO(UserPageDTO dbRecord);

    UserDetailVO convertUserDetailVO(User user);

    UserInfoVO convertUserInfoVO(UserDetailVO userDetail);
}
