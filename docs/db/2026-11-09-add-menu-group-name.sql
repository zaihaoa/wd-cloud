ALTER TABLE `sys_menu`
    ADD COLUMN `menu_group_name` VARCHAR(100) NULL DEFAULT NULL COMMENT '所属分类' AFTER `menu_name`;
