
--DDL

CREATE TABLE `user` (
                         `user_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                         `user_name` VARCHAR(50) NOT NULL,
                         `phone_number` VARCHAR(20) NOT NULL UNIQUE COMMENT '手機號碼(登入帳號)',
                         `email` VARCHAR(100) DEFAULT NULL,
                         `password` VARCHAR(255) NOT NULL COMMENT '加密後的密碼',
                         `cover_image` VARCHAR(255) DEFAULT NULL,
                         `biography` TEXT DEFAULT NULL,
                         `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `post` (
                         `post_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                         `user_id` BIGINT NOT NULL,
                         `content` TEXT NOT NULL,
                         `image` VARCHAR(255) DEFAULT NULL,
                         `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT `fk_post_userId_user_userId` FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
);

CREATE TABLE `comment` (
                            `comment_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                            `user_id` BIGINT NOT NULL,
                            `post_id` BIGINT NOT NULL,
                            `content` TEXT NOT NULL,
                            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT `fk_comment_userId_user_userId` FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
                            CONSTRAINT `fk_comment_postId_post_postId` FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`) ON DELETE CASCADE
);