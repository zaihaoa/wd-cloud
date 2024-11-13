package com.wd.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wd.file.entity.Attachment;
import com.wd.file.enums.AttachmentFileTypeEnum;
import com.wd.file.enums.AttachmentStoreTypeEnum;
import com.wd.file.mapper.AttachmentMapper;
import com.wd.file.service.AttachmentService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * <p>
 * 文件附件 服务实现类
 * </p>
 *
 * @author huangwenda
 * @since 2023-08-21
 */
@Service
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements AttachmentService {

    @SneakyThrows
    @Override
    public long upload(String fileName, InputStream in) {
        Assert.hasText(fileName, "文件名称不能为空");

        String filePath = LocalDateTime.now() + "_" + fileName;
        // todo
        File file = new File("/Users/huangwenda/Downloads/" + filePath);
        if (!file.exists()) {
            Assert.isTrue(file.createNewFile(), "文件创建失败:" + filePath);
        }

        FileUtils.copyInputStreamToFile(in, file);

        Attachment attachment = new Attachment();
        attachment.setStoreType(AttachmentStoreTypeEnum.LOCAL.getCode());
        attachment.setFileName(fileName);
        attachment.setFilePath(file.getAbsolutePath());
        attachment.setFileType(FilenameUtils.getExtension(fileName));
        attachment.setFileSize(file.length());
        baseMapper.insert(attachment);
        return attachment.getId();
    }

    @SneakyThrows
    @Override
    public void download(Long attachmentId, HttpServletResponse response) {
        Assert.notNull(attachmentId, "附件ID不能为空");

        Attachment attachment = baseMapper.selectById(attachmentId);
        Assert.notNull(attachmentId, "附件ID[" + attachmentId + "]不存在");

        String storeType = attachment.getStoreType();
        if (AttachmentStoreTypeEnum.LOCAL.getCode().equals(storeType)) {
            // 文件类型
            String fileType = attachment.getFileType();
            if (AttachmentFileTypeEnum.XLSX.getCode().equals(fileType)) {
                File file = new File(attachment.getFilePath());
                Assert.isTrue(file.exists(), "文件不存在:" + attachment.getFilePath());

                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                // URLEncoder.encode可以防止中文乱码
                String encode = URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8);
                response.setHeader("Content-disposition", "attachment;filename=" + encode);

                ServletOutputStream outputStream = response.getOutputStream();
                FileUtils.copyFile(file, outputStream);
            }
        } else if (AttachmentStoreTypeEnum.OSS.getCode().equals(storeType)) {

        }
    }
}
