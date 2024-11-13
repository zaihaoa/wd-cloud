package com.wd.system.system.convert;

import com.wd.system.system.dto.UserExportDTO;
import com.wd.system.system.dto.UserPageDTO;
import com.wd.system.system.dto.UserUpdateDTO;
import com.wd.system.system.entity.User;
import com.wd.system.system.vo.UserPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author huangwenda
 * @date 2023/8/31 14:20
 **/
@Mapper()
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    UserPageVO convertUserPageVO(UserPageDTO obj);

    List<UserPageVO> convertUserPageVOList(List<UserPageDTO> objs);

    List<UserExportDTO> convertUserExportDTOList(List<UserPageVO> objs);

    User convertUser(UserUpdateDTO obj);
}
