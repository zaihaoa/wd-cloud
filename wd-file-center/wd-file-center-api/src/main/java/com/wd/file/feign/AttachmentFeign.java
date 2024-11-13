package com.wd.file.feign;

import com.wd.common.core.R;
import com.wd.file.feign.dto.AttachmentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @author huangwenda
 * @date 2023/9/1 15:22
 **/
@Component
@FeignClient(name = "attachmentFeign")
public interface AttachmentFeign {

    R<AttachmentDTO> save(String fileName, InputStream in);
}
