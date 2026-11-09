package com.wd.common.oss.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OssFileLinkVO {

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件链接")
    private String fileLink;
}
