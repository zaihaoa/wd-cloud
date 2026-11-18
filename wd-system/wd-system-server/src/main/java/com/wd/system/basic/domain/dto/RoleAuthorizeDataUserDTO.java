package com.wd.system.basic.domain.dto;

import com.wd.system.basic.domain.enums.RoleAuthorizeOperationEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "角色用户数据授权参数")
public class RoleAuthorizeDataUserDTO {

    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正数")
    private Long roleId;

    @Schema(description = "本次选中的用户ID集合")
    @NotEmpty(message = "请选择需要授权或取消授权的用户")
    private List<@NotNull(message = "用户ID不能为空") @Positive(message = "用户ID必须为正数") Long> userIds;

    @Schema(description = "GRANT 增加授权，REVOKE 取消授权")
    @NotNull(message = "用户数据授权操作不能为空")
    private RoleAuthorizeOperationEnum operation;
}
