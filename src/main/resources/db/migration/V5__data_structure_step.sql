CREATE TABLE `step` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `created_at` datetime NOT NULL,
                        `updated_at` datetime NOT NULL,
                        `description` varchar(255) NOT NULL,
                        `photo_name` varchar(255) DEFAULT NULL,
                        `recipe_id` bigint DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `FKpwpbn24pd57073jm669d7dwt9` (`recipe_id`),
                        CONSTRAINT `FKpwpbn24pd57073jm669d7dwt9` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
