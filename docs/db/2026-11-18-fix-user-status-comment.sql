-- 更新列注释并移除空字符串默认值，保留现有数据。
ALTER TABLE `sys_user`
    MODIFY COLUMN `status` VARCHAR(100) NOT NULL COMMENT '状态(ENABLED:启用,DISABLED:禁用)';
