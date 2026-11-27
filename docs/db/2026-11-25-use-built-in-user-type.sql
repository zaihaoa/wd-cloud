-- 将旧版系统用户类型统一为 BUILT_IN。
UPDATE `sys_user`
SET `type` = 'BUILT_IN'
WHERE CAST(`type` AS BINARY) = 'SYSTEM';

ALTER TABLE `sys_user`
    MODIFY COLUMN `type` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '类型(BUILT_IN:系统用户,NORMAL:普通用户)';
