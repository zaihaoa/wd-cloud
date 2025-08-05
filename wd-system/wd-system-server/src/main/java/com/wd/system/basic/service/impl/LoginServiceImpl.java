package com.wd.system.basic.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.wd.common.core.context.SystemContext;
import com.wd.common.core.util.AssertUtil;
import com.wd.system.basic.domain.dto.LoginParamDTO;
import com.wd.system.basic.domain.entity.User;
import com.wd.system.basic.domain.enums.UserStatusEnum;
import com.wd.system.basic.domain.vo.LoginResultVO;
import com.wd.system.basic.service.LoginService;
import com.wd.system.basic.service.UserService;
import com.wd.system.basic.util.UserHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 *
 *
 * @author huangwenda
 * @date 2025/5/16 15:58
 **/
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserService userService;

    @Override
    public LoginResultVO login(LoginParamDTO loginParam) {
        String username = loginParam.getUsername();
        log.info("登录,username:{}", username);

        User user = userService.getByUsername(username);
        AssertUtil.notNull(user, "用户不存在");
        Assert.isTrue(UserStatusEnum.NORMAL.getCode().equals(user.getStatus()), "用户已被禁用");

        String password = loginParam.getPassword();
        Assert.isTrue(UserHelper.checkPassword(password, user.getPassword()), "密码不正确");

        // 登录
        StpUtil.login(user.getId());

        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        LoginResultVO resultVO = new LoginResultVO();
        resultVO.setUserId(user.getId());
        resultVO.setUsername(user.getUsername());
        resultVO.setName(user.getName());
        resultVO.setToken(tokenInfo.getTokenValue());

        return resultVO;
    }

    @Override
    public boolean logout() {
        log.info("登出:{}", SystemContext.getUserId());
        StpUtil.logout();
        return true;
    }
}
