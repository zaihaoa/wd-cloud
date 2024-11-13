package com.wd.file.feign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author huangwenda
 * @date 2023/9/1 15:23
 **/
@Getter
@Setter
@ToString
public class AttachmentDTO {

    @Schema(description = "附件ID", example = "123")
    private Long attachmentId;
}
