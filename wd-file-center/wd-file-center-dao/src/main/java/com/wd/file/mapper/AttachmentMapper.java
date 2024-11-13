package com.wd.file.mapper;

import com.wd.common.mybatisplus.SuperMapper;
import com.wd.file.entity.Attachment;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 文件附件 Mapper 接口
 * </p>
 *
 * @author huangwenda
 * @since 2023-08-21
 */
@Mapper
public interface AttachmentMapper extends SuperMapper<Attachment> {

}
