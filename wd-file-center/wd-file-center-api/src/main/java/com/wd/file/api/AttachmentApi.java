package com.wd.file.api;

import com.wd.common.core.RpcResponse;
import com.wd.file.api.dto.AttachmentDTO;

import java.io.InputStream;

/**
 * @author huangwenda
 * @date 2023/9/1 15:22
 **/
public interface AttachmentApi {

    RpcResponse<AttachmentDTO> save(String fileName, InputStream in);
}
