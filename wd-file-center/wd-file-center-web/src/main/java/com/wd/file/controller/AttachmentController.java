package com.wd.file.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.wd.common.core.R;
import com.wd.file.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 文件附件 前端控制器
 * </p>
 *
 * @author huangwenda
 * @since 2023-08-21
 */
@Tag(name = "文件附件")
@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;


    @Operation(summary = "上传附件")
    @ApiOperationSupport(order = 10)
    @PostMapping("/upload")
    public R<Long> upload(@RequestBody MultipartFile file) throws Exception {
        return R.success(attachmentService.upload(file.getName(), file.getInputStream()));
    }

    @Operation(summary = "下载附件")
    @ApiOperationSupport(order = 20)
    @GetMapping("/download/{attachmentId}")
    public void download(@PathVariable("attachmentId") Long attachmentId, HttpServletResponse response) {
        attachmentService.download(attachmentId, response);
    }
}
