package com.wd.system.basic.convert;

import com.wd.system.basic.domain.dto.UserExportDTO;
import com.wd.system.basic.domain.entity.User;
import com.wd.system.basic.domain.entity.Role;
import com.wd.system.basic.domain.vo.UserAuthorizePageVO;
import com.wd.system.basic.domain.vo.UserDetailVO;
import com.wd.system.basic.domain.vo.UserInfoVO;
import com.wd.system.system.api.basic.vo.UserRoleVO;
import com.wd.system.system.api.basic.dto.UserPermissionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.wd.system.basic.domain.dto.UserPageDTO;
import com.wd.system.basic.domain.dto.UserAuthorizePageDTO;
import com.wd.system.basic.domain.vo.UserPageVO;


@Mapper(componentModel = "spring")
public interface UserConverter {

    @Mapping(target = "roles", ignore = true)
    UserPageVO toUserPageVO(UserPageDTO dbRecord);

    UserAuthorizePageVO toUserAuthorizePageVO(UserAuthorizePageDTO dbRecord);

    @Mapping(target = "roles", ignore = true)
    UserDetailVO toUserDetailVO(User user);

    UserInfoVO toUserInfoVO(UserPermissionDTO userPermission);

    @Mapping(target = "ruleId", source = "id")
    @Mapping(target = "ruleCode", source = "code")
    @Mapping(target = "ruleName", source = "name")
    UserRoleVO toUserRoleVO(Role role);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "adminUser", ignore = true)
    @Mapping(target = "adminRole", ignore = true)
    @Mapping(target = "roleList", ignore = true)
    @Mapping(target = "authMenuIds", ignore = true)
    @Mapping(target = "authMenuPermissionList", ignore = true)
    @Mapping(target = "authFieldPermissionList", ignore = true)
    @Mapping(target = "authUserIds", ignore = true)
    UserPermissionDTO toUserPermissionDTO(User user);

    UserExportDTO toUserExportDTO(UserPageDTO record);
}
