package com.wd.common.oss.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.GetObjectRequest;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;
import com.wd.common.oss.properties.TencentOssProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.TreeMap;

@Slf4j
@Component
public class TencentOssClient {

    @Resource
    private TencentOssProperties tencentOssConfig;
    @Resource
    private COSClient cosClient;

    /**
     * 获取sts临时授权
     * @return OssStsAuthVO
     */
    public Response getStsAuth() {
        try {
            TreeMap<String, Object> config = new TreeMap<>();
            String stsHost = tencentOssConfig.getStsHost();
            if (StrUtil.isNotBlank(stsHost)) {
                config.put("host", stsHost);
            }
            config.put("secretId", tencentOssConfig.getSecretId());
            config.put("secretKey", tencentOssConfig.getSecretKey());
            config.put("durationSeconds", tencentOssConfig.getStsDurationSeconds());
            config.put("bucket", tencentOssConfig.getBucket());
            config.put("region", tencentOssConfig.getRegion());
            // 密钥的权限列表。简单上传和分片需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[]{
                    // 简单上传
                    "name/cos:PutObject",
                    "name/cos:PostObject",
                    // 分片上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);
            // 可以通过 allowPrefixes 指定前缀数组, 例子： a.jpg 或者 a/* 或者 * (使用通配符*存在重大安全风险, 请谨慎评估使用)
            config.put("allowPrefixes", new String[]{"*"});

            return CosStsClient.getCredential(config);
        } catch (Exception e) {
            log.error("腾讯云OSS获取STS临时授权失败", e);
            throw new RuntimeException("腾讯云OSS获取STS临时授权失败");
        }
    }

    /**
     * 生成签名URL
     *
     * @param bucket 桶
     * @param key           下载的文件名称（上传返回的key）
     * @param expirationDate 签名过期时间 默认1小时
     */
    public String getSignUrl(String bucket, String key, Date expirationDate) {
        URL url = cosClient.generatePresignedUrl(bucket, key, expirationDate);
        return url.toString();
    }

    /**
     * 本地文件上传
     *
     * @param inputStream io流
     * @param key         对象键(Key)是对象在存储桶中的唯一标识。
     */
    public String upload(InputStream inputStream, String key) {
        cosClient.putObject(tencentOssConfig.getBucket(), key, inputStream, null);

        IoUtil.close(inputStream);

        return key;
    }


    /**
     * 下载文件
     *
     * @param localFilePath 下载至本地文件目录+文件名称
     * @param bucket 桶
     * @param key           下载的文件名称（上传返回的key）
     */
    public void download(String localFilePath, String bucket, String key) {

        // 创建下载请求
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);

        // 下载到本地文件
        File downloadFile = new File(localFilePath);
        cosClient.getObject(getObjectRequest, downloadFile);
    }

    /**
     * 下载文件
     *
     * @param file 文件
     * @param bucket 桶
     * @param key           下载的文件名称（上传返回的key）
     */
    public void download(File file, String bucket, String key) {
        // 创建下载请求
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);

        // 下载到本地文件
        cosClient.getObject(getObjectRequest, file);
    }


    public void setObjectAcl(String key, CannedAccessControlList access) {
        cosClient.setObjectAcl(tencentOssConfig.getBucket(), key, access);
    }

    public String getObjectUrl(String key) {
        return cosClient.getObjectUrl(tencentOssConfig.getBucket(), key).toString();
    }
}
