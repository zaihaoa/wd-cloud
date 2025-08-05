package com.wd.system.basic.controller;

import com.wd.common.core.annotions.Folder;
import com.wd.common.core.model.R;
import com.wd.system.basic.domain.dto.LoginParamDTO;
import com.wd.system.basic.domain.vo.LoginResultVO;
import com.wd.system.basic.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangwenda
 * @date 2024/1/4 15:52
 **/
@Folder("登录注销")
@Tag(name = "登录注销")
@Slf4j
@RestController
@RequestMapping
public class LoginController {

    @Resource
    private LoginService loginService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public R<LoginResultVO> login(@RequestBody @Valid LoginParamDTO loginParam) {

        return R.success(loginService.login(loginParam));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public R<Boolean> logout() {
        return R.success(loginService.logout());
    }

}
