CREATE TABLE `ingredient` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `created_at` datetime NOT NULL,
                              `updated_at` datetime NOT NULL,
                              `ingredient_name` varchar(100) NOT NULL,
                              `measure_unit` varchar(255) NOT NULL,
                              `quantity` int NOT NULL,
                              `recipe_id` bigint DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              KEY `FKj0s4ywmqqqw4h5iommigh5yja` (`recipe_id`),
                              CONSTRAINT `FKj0s4ywmqqqw4h5iommigh5yja` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
