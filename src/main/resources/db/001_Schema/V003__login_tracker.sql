CREATE TABLE `login_tracker` (
     `id` bigint NOT NULL AUTO_INCREMENT,
     `user_name` varchar(250) COLLATE utf8mb3_unicode_ci NOT NULL,
     `status` varchar(256) COLLATE utf8mb3_unicode_ci NOT NULL,
     `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
     `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;