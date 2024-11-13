package com.wd.file.service;

import com.wd.common.mybatisplus.SuperService;
import com.wd.file.entity.Attachment;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;

/**
 * <p>
 * 文件附件 服务类
 * </p>
 *
 * @author huangwenda
 * @since 2023-08-21
 */
public interface AttachmentService extends SuperService<Attachment> {

    /**
     * 保存附件
     *
     * @param fileName 附件名称 例如:订单.xlsx
     * @param in       InputStream
     * @return 附件ID
     */
    long upload(String fileName, InputStream in);

    /**
     * 下载附件
     *
     * @param attachmentId 附件ID
     * @param response     HttpServletResponse
     */
    void download(Long attachmentId, HttpServletResponse response);

}
