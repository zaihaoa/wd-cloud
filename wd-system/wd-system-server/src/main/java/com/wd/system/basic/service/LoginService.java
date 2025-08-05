package com.wd.system.basic.service;


import com.wd.system.basic.domain.dto.LoginParamDTO;
import com.wd.system.basic.domain.vo.LoginResultVO;

/**
 * @author huangwenda
 * @date 2024/1/9 14:27
 **/
public interface LoginService {

    /**
     * 登录
     */
    LoginResultVO login(LoginParamDTO loginParam);

    /**
     * 登出
     *
     * @return 是否登出成功
     */
    boolean logout();

}
