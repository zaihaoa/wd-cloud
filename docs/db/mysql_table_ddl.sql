
-- 用户
drop table if exists `sys_user`;
CREATE TABLE `sys_user` (
                            `id` bigint unsigned NOT NULL COMMENT '主键ID',
                            `username` varchar(100) NOT NULL DEFAULT '' COMMENT '用户名',
                            `name` varchar(100) NOT NULL DEFAULT '' COMMENT '姓名',
                            `password` varchar(200) NOT NULL DEFAULT '' COMMENT '用户密码',
                            `status` varchar(100) NOT NULL DEFAULT '' COMMENT '状态(ENABLED:启用,DISABLED:禁用)',
                            `type` varchar(100) NOT NULL DEFAULT '' COMMENT '类型(BUILT_IN:内置用户,NORMAL:普通用户)',
                            `phone` varchar(100) DEFAULT NULL COMMENT '手机号码',
                            `email` varchar(200) DEFAULT NULL COMMENT '邮箱',
                            `delete_flag` bigint NOT NULL DEFAULT '0' COMMENT '删除标记，等于0表示没有删除，否则为删除',
                            `create_user_id` bigint NOT NULL COMMENT '创建用户ID',
                            `update_user_id` bigint NOT NULL COMMENT '更新用户ID',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`,`delete_flag`) USING BTREE,
                            UNIQUE KEY `uk_email` (`email`,`delete_flag`) USING BTREE,
                            UNIQUE KEY `uk_phone` (`phone`,`delete_flag`) USING BTREE
) ENGINE=InnoDB COMMENT='用户';


-- 用户和角色关联
drop table if exists `sys_user_role_relevance`;
CREATE TABLE `sys_user_role_relevance` (
                                           `id` bigint unsigned NOT NULL COMMENT '主键ID',
                                           `user_id` bigint NOT NULL COMMENT '用户ID',
                                           `role_id` bigint NOT NULL COMMENT '角色ID',
                                           `create_user_id` bigint NOT NULL COMMENT '创建人用户ID',
                                           `update_user_id` bigint NOT NULL COMMENT '更新人用户ID',
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           PRIMARY KEY (`id`),
                                           KEY `idx_user_id` (`user_id`),
                                           KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB COMMENT='用户和角色关联';


-- 菜单
drop table if exists `sys_menu`;
CREATE TABLE `sys_menu` (
                            `id` bigint unsigned NOT NULL COMMENT '主键ID',
                            `menu_type` varchar(100) NOT NULL DEFAULT '' COMMENT '类型(DIRECTORY:目录,MENU:菜单)',
                            `menu_name` varchar(100) NOT NULL DEFAULT '' COMMENT '菜单名称',
                            `menu_group_name` varchar(100) DEFAULT NULL COMMENT '所属分类',
                            `status` varchar(100) NOT NULL DEFAULT '' COMMENT '状态(ENABLED:启用,DISABLED:禁用)',
                            `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级ID',
                            `route_name` varchar(200) DEFAULT NULL COMMENT '路由名称',
                            `route_path` varchar(200) DEFAULT NULL COMMENT '路由路径',
                            `route_path_param` varchar(200) DEFAULT NULL COMMENT '路由路径参数',
                            `permission` varchar(200) NOT NULL DEFAULT '' COMMENT '权限标志',
                            `layout` varchar(200) DEFAULT NULL COMMENT '布局',
                            `component` varchar(200) NOT NULL DEFAULT '' COMMENT '组件',
                            `i18n_key` varchar(200) NOT NULL DEFAULT '' COMMENT '国际化KEY',
                            `sort_number` int NOT NULL DEFAULT '0' COMMENT '显示顺序，越小越优先',
                            `icon_type` varchar(100) NOT NULL DEFAULT '' COMMENT '图标类型',
                            `icon` varchar(100) NOT NULL DEFAULT '' COMMENT '图标',
                            `cache_route` tinyint(1) NOT NULL DEFAULT '0' COMMENT '缓存路由',
                            `constant_route` tinyint(1) NOT NULL DEFAULT '0' COMMENT '常量路由',
                            `hide_route` tinyint(1) NOT NULL DEFAULT '0' COMMENT '隐藏路由',
                            `highlight_route_name` varchar(200) DEFAULT NULL COMMENT '高亮路由名称',
                            `external_link` varchar(100) NOT NULL DEFAULT '' COMMENT '外链',
                            `route_param` json DEFAULT NULL COMMENT '路由参数',
                            `button` json DEFAULT NULL COMMENT '按钮',
                            `create_user_id` bigint NOT NULL COMMENT '创建人用户ID',
                            `update_user_id` bigint NOT NULL COMMENT '更新人用户ID',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_route_path` (`route_path`) USING BTREE,
                            KEY `idx_permission` (`permission`),
                            KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB COMMENT='菜单';

-- 角色
drop table if exists `sys_role`;
CREATE TABLE `sys_role` (
                            `id` bigint unsigned NOT NULL COMMENT '主键ID',
                            `code` varchar(100) NOT NULL DEFAULT '' COMMENT '角色编码',
                            `name` varchar(100) NOT NULL DEFAULT '' COMMENT '角色名称',
                            `status` varchar(100) NOT NULL DEFAULT '' COMMENT '状态(ENABLED:启用,DISABLED:禁用)',
                            `type` varchar(100) NOT NULL DEFAULT '' COMMENT '类型(NORMAL:普通,BUILT_IN:内置)',
                            `remark` varchar(1000) NOT NULL DEFAULT '' COMMENT '备注',
                            `permission_flag` json DEFAULT NULL COMMENT '权限标记',
                            `delete_flag` bigint NOT NULL DEFAULT '0' COMMENT '删除标记，等于0表示没有删除，否则为删除',
                            `create_user_id` bigint NOT NULL COMMENT '创建人用户ID',
                            `update_user_id` bigint NOT NULL COMMENT '更新人用户ID',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_code` (`code`,`delete_flag`) USING BTREE,
                            UNIQUE KEY `uk_name` (`name`,`delete_flag`) USING BTREE
) ENGINE=InnoDB COMMENT='角色';

-- 角色权限关联
drop table if exists `sys_role_permission_relevance`;
CREATE TABLE `sys_role_permission_relevance` (
                                                 `id` bigint unsigned NOT NULL COMMENT '主键ID',
                                                 `role_id` bigint NOT NULL COMMENT '角色ID',
                                                 `type` varchar(100) NOT NULL DEFAULT '' COMMENT '类型(MENU:菜单权限,FIELD:字段权限,DATA_USER:用户数据权限)',
                                                 `relevance_id` bigint DEFAULT NULL COMMENT '关联ID',
                                                 `relevance_value` varchar(200) NOT NULL DEFAULT '' COMMENT '关联值',
                                                 `create_user_id` bigint NOT NULL COMMENT '创建人用户ID',
                                                 `update_user_id` bigint NOT NULL COMMENT '更新人用户ID',
                                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                 PRIMARY KEY (`id`),
                                                 KEY `idx_role_id_type` (`role_id`,`type`)
) ENGINE=InnoDB COMMENT='角色权限关联';

-- 通用日志
drop table if exists `sys_common_log`;
CREATE TABLE `sys_common_log` (
                                  `id` bigint unsigned NOT NULL COMMENT '主键ID',
                                  `business_type` varchar(200) NOT NULL DEFAULT '' COMMENT '业务类型',
                                  `business_key` varchar(200) NOT NULL DEFAULT '' COMMENT '业务键',
                                  `operation_type` varchar(200) NOT NULL DEFAULT '' COMMENT '操作类型',
                                  `content` varchar(10000) NOT NULL DEFAULT '' COMMENT '操作内容',
                                  `create_user_id` bigint NOT NULL COMMENT '创建人用户ID',
                                  `update_user_id` bigint NOT NULL COMMENT '更新人用户ID',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_business_type` (`business_type`),
                                  KEY `idx_business_key` (`business_key`)
) ENGINE=InnoDB COMMENT='通用日志';

-- 字典
drop table if exists `sys_dictionary`;
CREATE TABLE `sys_dictionary` (
                                  `id` bigint unsigned NOT NULL COMMENT '主键ID',
                                  `code` varchar(100) NOT NULL DEFAULT '' COMMENT '编码',
                                  `desc` varchar(2000) NOT NULL DEFAULT '' COMMENT '描述',
                                  `status` varchar(100) NOT NULL DEFAULT '' COMMENT '状态(ENABLED:启用,DISABLED:禁用)',
                                  `remark` varchar(2000) NOT NULL DEFAULT '' COMMENT '备注',
                                  `delete_flag` bigint NOT NULL DEFAULT '0' COMMENT '删除标记，等于0表示没有删除，否则为删除',
                                  `create_user_id` bigint NOT NULL COMMENT '创建人用户ID',
                                  `update_user_id` bigint NOT NULL COMMENT '更新人用户ID',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_code` (`code`, `delete_flag`) USING BTREE
) ENGINE=InnoDB COMMENT='字典';

-- 字典明细
drop table if exists `sys_dictionary_item`;
CREATE TABLE `sys_dictionary_item` (
                                       `id` bigint unsigned NOT NULL COMMENT '主键ID',
                                       `dictionary_code` varchar(100) NOT NULL DEFAULT '' COMMENT '字典编码',
                                       `item_code` varchar(100) NOT NULL DEFAULT '' COMMENT '明细编码',
                                       `item_desc` varchar(2000) NOT NULL DEFAULT '' COMMENT '明细描述',
                                       `sort_number` int NOT NULL DEFAULT '0' COMMENT '显示顺序，越小越优先',
                                       `status` varchar(100) NOT NULL DEFAULT '' COMMENT '状态(ENABLED:启用,DISABLED:禁用)',
                                       `remark` varchar(2000) NOT NULL DEFAULT '' COMMENT '备注',
                                       `delete_flag` bigint NOT NULL DEFAULT '0' COMMENT '删除标记，等于0表示没有删除，否则为删除',
                                       `create_user_id` bigint NOT NULL COMMENT '创建人用户ID',
                                       `update_user_id` bigint NOT NULL COMMENT '更新人用户ID',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_dictionary_code_item_code` (`dictionary_code`, `item_code`, `delete_flag`) USING BTREE
) ENGINE=InnoDB COMMENT='字典明细';

-- 全局配置
drop table if exists `sys_global_config`;
CREATE TABLE `sys_global_config` (
                                     `id` bigint unsigned NOT NULL COMMENT '主键ID',
                                     `code` varchar(100) NOT NULL DEFAULT '' COMMENT '编码',
                                     `value` varchar(2000) NOT NULL DEFAULT '' COMMENT '值',
                                     `remark` varchar(2000) NOT NULL DEFAULT '' COMMENT '备注',
                                     `type` varchar(100) NOT NULL DEFAULT '' COMMENT '类型(SYSTEM:系统内置,NORMAL:普通)',
                                     `sort_number` int NOT NULL DEFAULT '0' COMMENT '显示顺序，越小越优先',
                                     `delete_flag` bigint NOT NULL DEFAULT '0' COMMENT '删除标记，等于0表示没有删除，否则为删除',
                                     `create_user_id` bigint NOT NULL COMMENT '创建人用户ID',
                                     `update_user_id` bigint NOT NULL COMMENT '更新人用户ID',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_code` (`code`, `delete_flag`) USING BTREE
) ENGINE=InnoDB COMMENT='全局配置';

-- 用户配置
DROP TABLE IF EXISTS `sys_user_config`;
CREATE TABLE `sys_user_config` (
    `id` bigint unsigned NOT NULL COMMENT '主键ID',
    `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
    `type` varchar(100) NOT NULL COMMENT '类型',
    `code` varchar(500) NOT NULL COMMENT '编码',
    `content` mediumtext NOT NULL COMMENT '内容',
    `create_user_id` bigint NOT NULL COMMENT '创建人用户ID',
    `update_user_id` bigint NOT NULL COMMENT '更新人用户ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_config` (`user_id`, `type`, `code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户配置';


-- 数据

-- 用户
INSERT INTO `sys_user` (`id`, `username`, `name`, `password`, `status`, `type`, `phone`, `email`, `delete_flag`, `create_user_id`, `update_user_id`, `create_time`, `update_time`) VALUES (1, 'admin', 'Admin', '$2a$10$6aXuZTjLdebHrZKQwmRJw.PwuWt.X0rQIZEW4umBdvrMV5uWPyfsi', 'ENABLED', 'BUILT_IN', NULL, NULL, 0, 0, 0, now(), now());

-- 角色
INSERT INTO `sys_role` (`id`, `code`, `name`, `status`, `type`, `remark`, `permission_flag`, `delete_flag`, `create_user_id`, `update_user_id`, `create_time`, `update_time`) VALUES (1, 'admin', '超级管理员', 'ENABLED', 'BUILT_IN', '', '[\"ALL_PERMISSION\"]', 0, 0, 0, now(), now());

-- 用户和角色关联
INSERT INTO `sys_user_role_relevance` (`id`, `user_id`, `role_id`, `create_user_id`, `update_user_id`, `create_time`, `update_time`) VALUES (1, 1, 1, 0, 0, now(), now());

-- 菜单
INSERT INTO `sys_menu` (`id`, `menu_type`, `menu_name`, `status`, `parent_id`, `route_name`, `route_path`, `route_path_param`, `permission`, `layout`, `component`, `i18n_key`, `sort_number`, `icon_type`, `icon`, `cache_route`, `constant_route`, `hide_route`, `highlight_route_name`, `external_link`, `route_param`, `button`, `create_user_id`, `update_user_id`, `create_time`, `update_time`) VALUES (1, 'DIRECTORY', '系统', 'ENABLED', 0, 'manage', '/manage', NULL, '', NULL, '', '', 1000, 'ICONIFY', 'carbon:cloud-service-management', 0, 0, 0, NULL, '', NULL, NULL, 0, 0, now(), now());
INSERT INTO `sys_menu` (`id`, `menu_type`, `menu_name`, `status`, `parent_id`, `route_name`, `route_path`, `route_path_param`, `permission`, `layout`, `component`, `i18n_key`, `sort_number`, `icon_type`, `icon`, `cache_route`, `constant_route`, `hide_route`, `highlight_route_name`, `external_link`, `route_param`, `button`, `create_user_id`, `update_user_id`, `create_time`, `update_time`) VALUES (2, 'MENU', '用户管理', 'ENABLED', 1, 'manage_user', '/manage/user', NULL, '', NULL, '', '', 1000, 'ICONIFY', 'ic:round-manage-accounts', 0, 0, 0, NULL, '', NULL, NULL, 0, 0, now(), now());
INSERT INTO `sys_menu` (`id`, `menu_type`, `menu_name`, `status`, `parent_id`, `route_name`, `route_path`, `route_path_param`, `permission`, `layout`, `component`, `i18n_key`, `sort_number`, `icon_type`, `icon`, `cache_route`, `constant_route`, `hide_route`, `highlight_route_name`, `external_link`, `route_param`, `button`, `create_user_id`, `update_user_id`, `create_time`, `update_time`) VALUES (3, 'MENU', '角色管理', 'ENABLED', 1, 'manage_role', '/manage/role', NULL, '', NULL, '', '', 2000, 'ICONIFY', 'carbon:user-role', 0, 0, 0, NULL, '', NULL, NULL, 0, 0, now(), now());
INSERT INTO `sys_menu` (`id`, `menu_type`, `menu_name`, `status`, `parent_id`, `route_name`, `route_path`, `route_path_param`, `permission`, `layout`, `component`, `i18n_key`, `sort_number`, `icon_type`, `icon`, `cache_route`, `constant_route`, `hide_route`, `highlight_route_name`, `external_link`, `route_param`, `button`, `create_user_id`, `update_user_id`, `create_time`, `update_time`) VALUES (4, 'MENU', '菜单管理', 'ENABLED', 1, 'manage_menu', '/manage/menu', NULL, '', NULL, '', '', 3000, 'ICONIFY', 'mdi:menu', 0, 0, 0, NULL, '', NULL, NULL, 0, 0, now(), now());
