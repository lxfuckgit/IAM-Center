CREATE TABLE `app_info` (
  `app_id` bigint NOT NULL,
  `app_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `app_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `app_contact` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `app_provider` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `app_status` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `company_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `app_update` (
  `id` bigint NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `app_id` int DEFAULT NULL,
  `app_sys` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_channel` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_contnet` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vers_num` int NOT NULL,
  `version` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `app_update_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `hy100` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `app_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `party_type_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role_type_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `external_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `come_from` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `invate_code` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_user_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_l70nwj71aptewic36bwi8gdky` (`invate_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `hy101` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `group_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `group_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `auth_status_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fax` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `legal_entity` varchar(25) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `license_image` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `license_no` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mobile_phone` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `organization_code` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `organization_image` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `registration_image` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `registration_no` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telephone` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `website` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK8chg8aj2l5b9oi0ge2ykv55kx` FOREIGN KEY (`id`) REFERENCES `hy100` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `hy102` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `first_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nick_name` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `birthday` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icon_url` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sfz` varchar(18) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sex` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mobile_phone` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKg2lfsfxsq6qnvdfj7h68f57ln` FOREIGN KEY (`id`) REFERENCES `hy100` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `hy701` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `address` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `consignee` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `emaill` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `geo_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sfmrdz` varchar(1) COLLATE utf8mb4_unicode_ci NOT NULL,
  `login_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mobile` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `zip_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `hy900` (
  `party_id` varchar(32) NOT NULL,
  `tag_type` varchar(16) DEFAULT NULL,
  `tags` varchar(50) DEFAULT NULL,
  `weight` int DEFAULT NULL,
  PRIMARY KEY (`party_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `oauth_client` (
  `client_id` varchar(32) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `client_key` varchar(32) DEFAULT NULL,
  `client_name` varchar(32) DEFAULT NULL,
  `client_secret` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `party_relation` (
  `party_id_from` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `party_id_to` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_id_from` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_id_to` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `relation_type_id` varchar(12) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sequence` int DEFAULT NULL,
  PRIMARY KEY (`party_id_from`,`party_id_to`,`role_id_from`,`role_id_to`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `sys_login` (
  `login_id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `login_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `login_pwd` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `login_alias` varchar(60) DEFAULT NULL,
  `icon_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `ext_login_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `ext_login_pwd` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `login_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `device_id` varchar(30) DEFAULT NULL,
  `device_idfa` varchar(30) DEFAULT NULL,
  `device_idfv` varchar(30) DEFAULT NULL,
  `device_ip` varchar(30) DEFAULT NULL,
  `device_mac` varchar(30) DEFAULT NULL,
  `from_channel` varchar(30) DEFAULT NULL,
  `version` varchar(12) NOT NULL,
  `expire_time` date DEFAULT NULL,
  `invite_code` varchar(50) DEFAULT NULL,
  `login_expire` bigint DEFAULT NULL,
  `login_state` varchar(12) NOT NULL,
  `party_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rst_source` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`login_id`),
  UNIQUE KEY `UKlcbvqk1gldipojil3pdny6mt4` (`app_id`,`login_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `sys_login_log` (
  `id` varchar(32) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `app_id` varchar(32) NOT NULL,
  `device_id` varchar(30) DEFAULT NULL,
  `device_idfa` varchar(30) DEFAULT NULL,
  `device_idfv` varchar(30) DEFAULT NULL,
  `device_ip` varchar(30) DEFAULT NULL,
  `device_mac` varchar(30) DEFAULT NULL,
  `from_channel` varchar(30) DEFAULT NULL,
  `version` varchar(12) NOT NULL,
  `chid` varchar(16) DEFAULT NULL,
  `client_ip` varchar(16) DEFAULT NULL,
  `come_from` varchar(16) DEFAULT NULL,
  `login_date` varchar(255) DEFAULT NULL,
  `login_desc` varchar(255) DEFAULT NULL,
  `login_id` varchar(32) DEFAULT NULL,
  `login_result` varchar(255) DEFAULT NULL,
  `login_type` varchar(255) DEFAULT NULL,
  `loginpwd` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `sys_login_role` (
  `login_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`login_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `sys_pwd_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `app_id` varchar(32) NOT NULL,
  `device_id` varchar(30) DEFAULT NULL,
  `device_idfa` varchar(30) DEFAULT NULL,
  `device_idfv` varchar(30) DEFAULT NULL,
  `device_ip` varchar(30) DEFAULT NULL,
  `device_mac` varchar(30) DEFAULT NULL,
  `from_channel` varchar(30) DEFAULT NULL,
  `version` varchar(12) NOT NULL,
  `login_id` bigint NOT NULL,
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `sys_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `res_type` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `res_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `res_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `res_icon` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `res_url` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `res_remark` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_id` bigint DEFAULT NULL,
  `sequence` int DEFAULT NULL,
  `status_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_c71kwhhpskpwvifk48ihjkapd` (`res_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` varchar(30) NOT NULL,
  `role_code` varchar(30) NOT NULL,
  `role_name` varchar(50) NOT NULL,
  `role_remark` varchar(100) DEFAULT NULL,
  `status_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `creator_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sequence` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jqdita2l45v2gglry7bp8kl1f` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `sys_role_resource` (
  `role_id` bigint NOT NULL,
  `resource_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`resource_id`),
  KEY `FKkj7e3cva1e2s3nsd0yghpbsnk` (`resource_id`),
  CONSTRAINT `FK7urjh5xeujvp29nihwbs5b9kr` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
  CONSTRAINT `FKkj7e3cva1e2s3nsd0yghpbsnk` FOREIGN KEY (`resource_id`) REFERENCES `sys_resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `sys_session` (
  `token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `app_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_id` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `device_idfa` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `device_idfv` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `device_ip` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `device_mac` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `from_channel` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `version` varchar(12) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expire_time` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

