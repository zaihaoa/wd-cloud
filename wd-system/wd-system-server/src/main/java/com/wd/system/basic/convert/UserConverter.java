package com.wd.system.basic.convert;

import com.wd.system.basic.domain.entity.User;
import com.wd.system.basic.domain.entity.Role;
import com.wd.system.basic.domain.vo.UserDetailVO;
import com.wd.system.basic.domain.vo.UserInfoVO;
import com.wd.system.system.api.basic.vo.UserRoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.wd.system.basic.domain.dto.UserPageDTO;
import com.wd.system.basic.domain.vo.UserPageVO;


@Mapper(componentModel = "spring")
public interface UserConverter {

    @Mapping(target = "roles", ignore = true)
    UserPageVO convertUserPageVO(UserPageDTO dbRecord);

    @Mapping(target = "roles", ignore = true)
    UserDetailVO convertUserDetailVO(User user);

    UserInfoVO convertUserInfoVO(UserDetailVO userDetail);

    @Mapping(target = "ruleId", source = "id")
    @Mapping(target = "ruleCode", source = "code")
    @Mapping(target = "ruleName", source = "name")
    UserRoleVO convertUserRoleVO(Role role);
}
