CREATE TABLE `recipe` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `created_at` datetime NOT NULL,
                          `updated_at` datetime NOT NULL,
                          `cook_time` int NOT NULL,
                          `recipe_name` varchar(75) NOT NULL,
                          `recipe_photo_name` varchar(255) DEFAULT NULL,
                          `author_id` bigint DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `FKlvmxb2tmwa9979nk3yexb805p` (`author_id`),
                          CONSTRAINT `FKlvmxb2tmwa9979nk3yexb805p` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
