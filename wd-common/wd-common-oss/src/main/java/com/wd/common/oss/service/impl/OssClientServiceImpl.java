package com.wd.common.oss.service.impl;

import com.qcloud.cos.model.CannedAccessControlList;
import com.tencent.cloud.Response;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.oss.model.OssFileLinkVO;
import com.wd.common.oss.model.OssStsAuthVO;
import com.wd.common.oss.properties.TencentOssProperties;
import com.wd.common.oss.service.OssClientService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class OssClientServiceImpl implements OssClientService {

    // 临时文件夹
    private static final String temporaryFolder = "/temporary";

    @Resource
    private TencentOssProperties tencentOssConfig;
    @Resource
    private TencentOssClient tencentOssClient;

    @Override
    public OssStsAuthVO getStsAuth() {
        Response response = tencentOssClient.getStsAuth();
        if (Objects.isNull(response)) {
            return null;
        }
        OssStsAuthVO ossStsAuthVO = new OssStsAuthVO();
        ossStsAuthVO.setToken(response.credentials.token);
        ossStsAuthVO.setSessionToken(response.credentials.sessionToken);
        ossStsAuthVO.setTmpSecretId(response.credentials.tmpSecretId);
        ossStsAuthVO.setTmpSecretKey(response.credentials.tmpSecretKey);
        ossStsAuthVO.setBucket(tencentOssConfig.getBucket());
        ossStsAuthVO.setRegion(tencentOssConfig.getRegion());
        ossStsAuthVO.setHost(tencentOssConfig.getHost());
        ossStsAuthVO.setDuration(tencentOssConfig.getStsDurationSeconds());
        ossStsAuthVO.setNowTimeMillis(System.currentTimeMillis());
        return ossStsAuthVO;
    }

    @Override
    public OssFileLinkVO getSignUrl(String url, Date expirationDate) {
        AssertUtil.hasText(url, "文件路径不能为空");
        String decodedUrl = UriUtils.decode(url, StandardCharsets.UTF_8);
        if (Objects.isNull(expirationDate)) {
            int durationSeconds = Optional.ofNullable(tencentOssConfig.getStsDurationSeconds()).orElse(1800);
            expirationDate = Date.from(LocalDateTime.now().plusSeconds(durationSeconds).atZone(ZoneId.systemDefault()).toInstant());
        }
        String fileLink = tencentOssClient.getSignUrl(tencentOssConfig.getBucket(), decodedUrl, expirationDate);
        String fileName = StringUtils.substringBefore(fileLink, "?");
        fileName = UriUtils.decode(StringUtils.substringAfterLast(fileName, "/"), StandardCharsets.UTF_8);
        return new OssFileLinkVO(fileName, fileLink);
    }

    @Override
    public String upload(InputStream inputStream, String key) {
        tencentOssClient.upload(inputStream, key);
        return key;
    }

    @Override
    public String uploadTemporary(InputStream inputStream, String key) {
        String newKey = temporaryFolder + key;
        tencentOssClient.upload(inputStream, newKey);
        return newKey;
    }

    /**
     * 文件下载
     * @param localFilePath 下载至本地文件目录+文件名称
     * @param url           下载的文件名称（上传返回的url）
     */
    @Override
    public void download(String localFilePath, String url) {
        tencentOssClient.download(localFilePath, tencentOssConfig.getBucket(), this.getFileKeyFromUrl(url));
    }

    @Override
    public void download(File file, String url) {
        log.info("下载文件url:{}", url);
        tencentOssClient.download(file, tencentOssConfig.getBucket(), this.getFileKeyFromUrl(url));
    }


    public String getFileKeyFromUrl(String url) {
        try {
            // 如果url不是以"/"开头，则补上"/"
            if (url != null && !url.startsWith("/")) {
                url = "/" + url;
            }
            return new URL("https://" + tencentOssConfig.getHost() + url).getPath();
        } catch (Exception e) {
            log.error("根据文件url获取文件key失败", e);
            throw new RuntimeException("根据文件url获取文件key失败");
        }
    }

    /**
     * 设置对象的 ACL 为公有读（PublicRead）
     * @param url
     */
    public void setObjectAclPublicRead(String url) {
        tencentOssClient.setObjectAcl(url, CannedAccessControlList.PublicRead);
    }

    public String getObjectUrl(String url) {
        return tencentOssClient.getObjectUrl(url);
    }
}
