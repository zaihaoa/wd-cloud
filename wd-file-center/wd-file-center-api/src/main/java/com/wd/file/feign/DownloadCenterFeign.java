package com.wd.file.feign;

import com.wd.common.core.R;
import com.wd.file.feign.dto.DownloadCenterCreateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author huangwenda
 * @date 2023/9/1 18:27
 **/
@Component
@FeignClient(name = "downloadCenterFeign")
public interface DownloadCenterFeign {

    @PostMapping("/create")
    R<Long> onCreate(DownloadCenterCreateDTO createParam);

    @PostMapping("/completed")
    R<Boolean> onCompleted(long downloadId, long attachmentId);

    @PostMapping("/error")
    R<Boolean> onError(long downloadId, String memo);

}
