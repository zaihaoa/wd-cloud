package com.wd.file.service;

import com.wd.common.mybatisplus.SuperService;
import com.wd.file.api.dto.DownloadCenterCreateDTO;
import com.wd.file.entity.DownloadCenter;

/**
 * <p>
 * 文件下载 服务类
 * </p>
 *
 * @author huangwenda
 * @since 2023-08-21
 */
public interface DownloadCenterService extends SuperService<DownloadCenter> {

    long onCreate(DownloadCenterCreateDTO createParam);

    void onCompleted(long downloadId, long attachmentId);

    void onError(long downloadId, String memo);

}
