package com.wd.system.controller.system;

import com.wd.common.core.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangwenda
 * @date 2023/3/10 14:44
 **/
@Tag(name = "登录")
@RestController
public class LoginController {

    @Operation(summary = "登录")
    @PostMapping("/login")
    public R<Object> login() {
        return R.success(true);
    }

}
