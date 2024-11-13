package com.wd.file.enums;

import com.wd.common.core.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangwenda
 * @date 2023/8/21 14:55
 **/
@Getter
@AllArgsConstructor
public enum AttachmentStoreTypeEnum implements CodeDescEnum<String> {

    LOCAL("LOCAL", "本地服务器"),
    OSS("OSS", "OSS服务器"),
    ;

    private final String code;

    private final String desc;
}
