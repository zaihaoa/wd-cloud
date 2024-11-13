package com.wd.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wd.common.core.BaseUserTimeEntity;
import com.wd.file.enums.AttachmentStoreTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 文件附件
 * </p>
 *
 * @author huangwenda
 * @since 2023-08-21
 */
@Getter
@Setter
@TableName("file_attachment")
public class Attachment extends BaseUserTimeEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 存储类型(LOCAL:本地服务器,OSS:OSS服务器)
     * {@link AttachmentStoreTypeEnum}
     */
    @TableField("store_type")
    private String storeType;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件大小(KB)
     */
    @TableField("file_size")
    private Long fileSize;
}
