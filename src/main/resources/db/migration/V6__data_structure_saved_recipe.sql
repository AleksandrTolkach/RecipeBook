CREATE TABLE `saved_recipe` (
                                `recipe_id` bigint NOT NULL,
                                `user_id` bigint NOT NULL,
                                KEY `FKdiaau08v9b6scuas77irixs8a` (`user_id`),
                                KEY `FKh8edja741rr36wsvqka051dgf` (`recipe_id`),
                                CONSTRAINT `FKdiaau08v9b6scuas77irixs8a` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                                CONSTRAINT `FKh8edja741rr36wsvqka051dgf` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
