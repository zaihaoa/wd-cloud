package com.wd.system.system.convert;

import com.wd.system.system.dto.UserExportDTO;
import com.wd.system.system.dto.UserPageDTO;
import com.wd.system.system.dto.UserUpdateDTO;
import com.wd.system.system.entity.User;
import com.wd.system.system.vo.UserPageVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-13T18:01:39+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.1 (Oracle Corporation)"
)
@Component
public class UserConverterImpl implements UserConverter {

    @Override
    public UserPageVO convertUserPageVO(UserPageDTO obj) {
        if ( obj == null ) {
            return null;
        }

        UserPageVO userPageVO = new UserPageVO();

        userPageVO.setId( obj.getId() );
        userPageVO.setLoginName( obj.getLoginName() );
        userPageVO.setRealName( obj.getRealName() );
        userPageVO.setStatus( obj.getStatus() );
        userPageVO.setType( obj.getType() );
        userPageVO.setSex( obj.getSex() );
        userPageVO.setTel( obj.getTel() );
        userPageVO.setEmail( obj.getEmail() );
        userPageVO.setCreateTime( obj.getCreateTime() );
        userPageVO.setUpdateTime( obj.getUpdateTime() );

        return userPageVO;
    }

    @Override
    public List<UserPageVO> convertUserPageVOList(List<UserPageDTO> objs) {
        if ( objs == null ) {
            return null;
        }

        List<UserPageVO> list = new ArrayList<UserPageVO>( objs.size() );
        for ( UserPageDTO userPageDTO : objs ) {
            list.add( convertUserPageVO( userPageDTO ) );
        }

        return list;
    }

    @Override
    public List<UserExportDTO> convertUserExportDTOList(List<UserPageVO> objs) {
        if ( objs == null ) {
            return null;
        }

        List<UserExportDTO> list = new ArrayList<UserExportDTO>( objs.size() );
        for ( UserPageVO userPageVO : objs ) {
            list.add( userPageVOToUserExportDTO( userPageVO ) );
        }

        return list;
    }

    @Override
    public User convertUser(UserUpdateDTO obj) {
        if ( obj == null ) {
            return null;
        }

        User user = new User();

        user.setLoginName( obj.getLoginName() );
        user.setRealName( obj.getRealName() );
        user.setPassword( obj.getPassword() );
        user.setType( obj.getType() );
        user.setAvatarImageUrl( obj.getAvatarImageUrl() );
        user.setSex( obj.getSex() );
        user.setTel( obj.getTel() );
        user.setEmail( obj.getEmail() );

        return user;
    }

    protected UserExportDTO userPageVOToUserExportDTO(UserPageVO userPageVO) {
        if ( userPageVO == null ) {
            return null;
        }

        UserExportDTO userExportDTO = new UserExportDTO();

        userExportDTO.setLoginName( userPageVO.getLoginName() );
        userExportDTO.setRealName( userPageVO.getRealName() );
        userExportDTO.setStatusDesc( userPageVO.getStatusDesc() );
        userExportDTO.setTypeDesc( userPageVO.getTypeDesc() );
        userExportDTO.setSexDesc( userPageVO.getSexDesc() );
        userExportDTO.setTel( userPageVO.getTel() );
        userExportDTO.setEmail( userPageVO.getEmail() );
        userExportDTO.setCreateTime( userPageVO.getCreateTime() );
        userExportDTO.setUpdateTime( userPageVO.getUpdateTime() );

        return userExportDTO;
    }
}
