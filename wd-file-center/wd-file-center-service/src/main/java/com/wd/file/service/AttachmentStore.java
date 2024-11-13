package com.wd.file.service;


import com.wd.file.enums.AttachmentStoreTypeEnum;

import java.io.InputStream;

/**
 * 附加存储方式
 *
 * @author huangwenda
 * @date 2023/8/21 19:00
 **/
public interface AttachmentStore {

    /**
     * 附件存储类型
     *
     * @return 类型
     */
    AttachmentStoreTypeEnum storeType();

    /**
     * 上传文件
     * @param filename 文件名称
     * @param in InputStream
     */
    void upload(String filename, InputStream in);
}
