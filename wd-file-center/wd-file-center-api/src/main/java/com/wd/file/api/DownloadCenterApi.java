package com.wd.file.api;

import com.wd.common.core.RpcResponse;
import com.wd.file.api.dto.DownloadCenterCreateDTO;

/**
 * @author huangwenda
 * @date 2023/9/1 18:27
 **/
public interface DownloadCenterApi {

    RpcResponse<Long> onCreate(DownloadCenterCreateDTO createParam);

    RpcResponse<Boolean> onCompleted(long downloadId, long attachmentId);

    RpcResponse<Boolean> onError(long downloadId, String memo);
}
