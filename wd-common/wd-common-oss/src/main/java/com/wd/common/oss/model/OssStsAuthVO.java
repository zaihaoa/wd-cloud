package com.wd.common.oss.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OssStsAuthVO {
    @Schema(description = "临时访问ID")
    private String tmpSecretId;

    @Schema(description = "临时访问KEY")
    private String tmpSecretKey;

    @Schema(description = "临时访问Token")
    private String token;

    @Schema(description = "临时访问SessionToken")
    private String sessionToken;

    @Schema(description = "有效期(秒)")
    private Integer duration;

    @Schema(description = "bucket")
    private String bucket;

    @Schema(description = "region")
    private String region;

    @Schema(description = "host")
    private String host;

    @Schema(description = "当前时间戳")
    private Long nowTimeMillis;
}
