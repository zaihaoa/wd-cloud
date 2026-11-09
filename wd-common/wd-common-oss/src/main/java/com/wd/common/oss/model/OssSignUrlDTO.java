package com.wd.common.oss.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OssSignUrlDTO {

    @Schema(description = "文件路径", example = "/te/sd/123.png")
    private String filePath;
}
