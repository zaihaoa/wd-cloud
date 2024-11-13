package com.wd.file.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author huangwenda
 * @date 2023/8/21 17:40
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DownloadCenterCreateDTO {

    @Schema(description = "下载类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户信息")
    private String downloadType;

    @Schema(description = "下载方式,默认手动(JOB:定时任务,MANUAL:手动)", example = "用户信息")
    private String downloadWay;

    @Schema(description = "备注", example = "test-memo")
    private String memo;

    public DownloadCenterCreateDTO(String downloadType) {
        this.downloadType = downloadType;
    }
}
