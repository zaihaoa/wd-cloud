package com.wd.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.file.api.dto.DownloadCenterCreateDTO;
import com.wd.file.entity.Attachment;
import com.wd.file.entity.DownloadCenter;
import com.wd.file.enums.DownloadCenterStatusEnum;
import com.wd.file.enums.DownloadCenterWayEnum;
import com.wd.file.mapper.DownloadCenterMapper;
import com.wd.file.service.AttachmentService;
import com.wd.file.service.DownloadCenterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * <p>
 * 文件下载 服务实现类
 * </p>
 *
 * @author huangwenda
 * @since 2023-08-21
 */
@Service
public class DownloadCenterServiceImpl extends ServiceImpl<DownloadCenterMapper, DownloadCenter> implements DownloadCenterService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private AttachmentService attachmentService;


    @Override
    public long onCreate(DownloadCenterCreateDTO createParam) {
        String downloadType = createParam.getDownloadType();
        Assert.isTrue(StringUtils.isNotBlank(downloadType), "下载类型不能为空");

        String downloadWay = createParam.getDownloadWay();
        if (StringUtils.isNotBlank(downloadWay)) {
            Assert.isTrue(CodeDescEnumUtil.isExist(DownloadCenterWayEnum.class, downloadWay), "下载方式不正确:" + downloadWay);
        } else {
            downloadWay = DownloadCenterWayEnum.MANUAL.getCode();
        }

        DownloadCenter download = new DownloadCenter();
        download.setCode(generateCode());
        download.setDownloadType(downloadType);

        download.setDownloadWay(downloadWay);
        download.setStatus(DownloadCenterStatusEnum.PROCESS.getCode());
        download.setMemo(createParam.getMemo());
        download.setProgressBar(0);

        baseMapper.insert(download);
        return download.getId();
    }

    @Override
    public void onCompleted(long downloadId, long attachmentId) {
        DownloadCenter download = baseMapper.selectById(downloadId);
        Assert.notNull(download, "文件下载ID不存在:" + downloadId);

        Attachment attachment = attachmentService.getById(attachmentId);
        Assert.notNull(attachment, "附件ID不存在:" + attachmentId);

        DownloadCenter updateDownload = new DownloadCenter();
        updateDownload.setId(downloadId);
        updateDownload.setStatus(DownloadCenterStatusEnum.SUCCESS.getCode());
        updateDownload.setAttachmentId(attachmentId);
        updateDownload.setFileName(attachment.getFileName());
        updateDownload.setProgressBar(100);
        updateDownload.setCompletedTime(LocalDateTime.now());

        baseMapper.updateById(updateDownload);
    }

    @Override
    public void onError(long downloadId, String memo) {
        DownloadCenter download = baseMapper.selectById(downloadId);
        Assert.notNull(download, "文件下载ID不存在:" + downloadId);

        DownloadCenter updateDownload = new DownloadCenter();
        updateDownload.setId(downloadId);
        updateDownload.setStatus(DownloadCenterStatusEnum.FAILURE.getCode());
        updateDownload.setProgressBar(100);
        updateDownload.setCompletedTime(LocalDateTime.now());
        updateDownload.setMemo(memo);

        baseMapper.updateById(updateDownload);
    }

    private String generateCode() {
        Long increment = redisTemplate.opsForValue().increment("file:download");
        return String.format("%06d", increment);
    }
}
