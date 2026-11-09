package com.wd.common.oss.service;


import com.wd.common.oss.model.OssFileLinkVO;
import com.wd.common.oss.model.OssStsAuthVO;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

public interface OssClientService {

    /**
     * 获取sts临时授权
     * @return OssStsAuthVO
     */
    OssStsAuthVO getStsAuth();

    /**
     * 生成签名URL
     *
     * @param filePath 文件路径
     * @param expirationDate 过期时间
     */
    OssFileLinkVO getSignUrl(String filePath, Date expirationDate);

    /**
     * 文件上传
     * @param inputStream 输入流
     * @param key 文件key
     */
    String upload(InputStream inputStream, String key);

    /**
     * 临时文件上传
     * @param inputStream 输入流
     * @param key 文件key
     */
    String uploadTemporary(InputStream inputStream, String key);

    /**
     * 下载文件
     *
     * @param localFilePath 下载至本地文件目录+文件名称
     * @param url           下载的文件名称（上传返回的url）
     */
    void download(String localFilePath, String url);

    /**
     * 下载文件
     *
     * @param file 文件
     * @param url 下载的文件名称（上传返回的url）
     */
    void download(File file, String url);

    String getFileKeyFromUrl(String url);

    /**
     * 设置对象 ACL 为公有读（PublicRead）
     * @param url
     */
    void setObjectAclPublicRead(String url);

    String getObjectUrl(String url);
}
