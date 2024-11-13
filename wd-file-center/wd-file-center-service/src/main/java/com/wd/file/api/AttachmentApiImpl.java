package com.wd.file.api;

import com.wd.common.core.RpcResponse;
import com.wd.file.api.dto.AttachmentDTO;
import org.apache.dubbo.config.annotation.DubboService;

import java.io.InputStream;

/**
 * @author huangwenda
 * @date 2023/9/1 15:30
 **/
@DubboService
public class AttachmentApiImpl implements AttachmentApi {

    @Override
    public RpcResponse<AttachmentDTO> save(String fileName, InputStream in) {
        return null;
    }
}
