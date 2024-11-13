package com.wd.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wd.common.core.BaseUserTimeEntity;
import com.wd.file.enums.DownloadCenterStatusEnum;
import com.wd.file.enums.DownloadCenterWayEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 文件下载
 * </p>
 *
 * @author huangwenda
 * @since 2023-08-21
 */
@Getter
@Setter
@TableName("file_download_center")
public class DownloadCenter extends BaseUserTimeEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 下载类型
     */
    @TableField("download_type")
    private String downloadType;

    /**
     * 下载方式(JOB:定时任务,MANUAL:手动)
     * {@link DownloadCenterWayEnum}
     */
    @TableField("download_way")
    private String downloadWay;

    /**
     * 状态(PROCESS:处理中,SUCCESS:成功,FAILURE:失败)
     * {@link DownloadCenterStatusEnum}
     */
    @TableField("status")
    private String status;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 附件ID
     */
    @TableField("attachment_id")
    private Long attachmentId;

    /**
     * 备注
     */
    @TableField("memo")
    private String memo;

    /**
     * 进度条
     */
    @TableField("progress_bar")
    private Integer progressBar;

    /**
     * 完成时间
     */
    @TableField("completed_time")
    private LocalDateTime completedTime;
}
