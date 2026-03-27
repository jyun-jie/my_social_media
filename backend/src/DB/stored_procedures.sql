-- =============================================
-- Stored Procedure 定義
-- 用途：將資料庫操作邏輯封裝在資料庫端
-- =============================================
DELIMITER $$
-- 刪除舊的 Procedure（如果存在）
DROP PROCEDURE IF EXISTS sp_register_user$$
DROP PROCEDURE IF EXISTS sp_login_user$$
DROP PROCEDURE IF EXISTS sp_get_user_by_id$$
DROP PROCEDURE IF EXISTS sp_get_user_by_phone$$
DROP PROCEDURE IF EXISTS sp_create_post$$
DROP PROCEDURE IF EXISTS sp_get_all_posts$$
DROP PROCEDURE IF EXISTS sp_get_post_by_id$$
DROP PROCEDURE IF EXISTS sp_update_post$$
DROP PROCEDURE IF EXISTS sp_delete_post$$
DROP PROCEDURE IF EXISTS sp_add_comment$$
DROP PROCEDURE IF EXISTS sp_get_comments_by_post_id$$
DROP PROCEDURE IF EXISTS sp_delete_comment$$

-- =============================================
-- User 相關 Stored Procedure
-- =============================================

-- sp_register_user: 註冊新使用者
-- 參數：p_user_name, p_phone_number, p_email, p_password
-- 說明：將新使用者資料插入 user 表
CREATE PROCEDURE sp_register_user(
    IN p_user_name VARCHAR(50),
    IN p_phone_number VARCHAR(20),
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(255)
)
BEGIN
INSERT INTO user (user_name, phone_number, email, password)
VALUES (p_user_name, p_phone_number, p_email, p_password);
END$$

-- sp_login_user: 登入驗證
-- 參數：p_phone_number
-- 說明：根據手機號碼查詢使用者資訊（密碼驗證在 Java 端進行）
CREATE PROCEDURE sp_login_user(
    IN p_phone_number VARCHAR(20)
)
BEGIN
SELECT user_id, user_name, phone_number, email, password
FROM user
WHERE phone_number = p_phone_number;
END$$

-- sp_get_user_by_id: 根據 ID 取得使用者
-- 參數：p_user_id
-- 說明：查詢指定使用者的基本資訊
CREATE PROCEDURE sp_get_user_by_id(
    IN p_user_id BIGINT
)
BEGIN
SELECT user_id, user_name, phone_number, email, cover_image, biography, created_at
FROM user
WHERE user_id = p_user_id;
END$$

-- sp_get_user_by_phone: 根據手機號碼取得使用者
-- 參數：p_phone_number
-- 說明：檢查手機號碼是否已註冊
CREATE PROCEDURE sp_get_user_by_phone(
    IN p_phone_number VARCHAR(20)
)
BEGIN
SELECT user_id, user_name, phone_number, email
FROM user
WHERE phone_number = p_phone_number;
END$$

-- =============================================
-- Post 相關 Stored Procedure
-- =============================================

-- sp_create_post: 新增發文
-- 參數：p_user_id, p_content, p_image
-- 說明：將新發文資料插入 post 表
CREATE PROCEDURE sp_create_post(
    IN p_user_id BIGINT,
    IN p_content TEXT,
    IN p_image VARCHAR(255)
)
BEGIN
    DECLARE new_post_id BIGINT;
INSERT INTO post (user_id, content, image)
VALUES (p_user_id, p_content, p_image);
SET new_post_id = LAST_INSERT_ID();
SELECT p.post_id, p.user_id, u.user_name, p.content, p.image, p.created_at
FROM post p
         JOIN user u ON p.user_id = u.user_id
WHERE p.post_id = new_post_id;
END$$

-- sp_get_all_posts: 取得所有發文
-- 說明：按時間倒序列出所有發文，包含發文者名稱
CREATE PROCEDURE sp_get_all_posts()
BEGIN
SELECT p.post_id, p.user_id, u.user_name, p.content, p.image, p.created_at
FROM post p
         JOIN user u ON p.user_id = u.user_id
ORDER BY p.created_at DESC;
END$$

-- sp_get_post_by_id: 根據 ID 取得發文
-- 參數：p_post_id
-- 說明：查詢指定發文的詳細資訊
CREATE PROCEDURE sp_get_post_by_id(
    IN p_post_id BIGINT
)
BEGIN
SELECT p.post_id, p.user_id, u.user_name, p.content, p.image, p.created_at
FROM post p
         JOIN user u ON p.user_id = u.user_id
WHERE p.post_id = p_post_id;
END$$

-- sp_update_post: 更新發文
-- 參數：p_post_id, p_user_id, p_content, p_image
-- 說明：更新指定發文的內容（需驗證是否為發文者）
CREATE PROCEDURE sp_update_post(
    IN p_post_id BIGINT,
    IN p_user_id BIGINT,
    IN p_content TEXT,
    IN p_image VARCHAR(255)
)
BEGIN
UPDATE post
SET content = p_content, image = p_image
WHERE post_id = p_post_id AND user_id = p_user_id;

SELECT p.post_id, p.user_id, u.user_name, p.content, p.image, p.created_at
FROM post p
         JOIN user u ON p.user_id = u.user_id
WHERE p.post_id = p_post_id;
END$$

-- sp_delete_post: 刪除發文
-- 參數：p_post_id, p_user_id
-- 說明：刪除指定發文（需驗證是否為發文者）
CREATE PROCEDURE sp_delete_post(
    IN p_post_id BIGINT,
    IN p_user_id BIGINT
)
BEGIN
DELETE FROM post
WHERE post_id = p_post_id AND user_id = p_user_id;
END$$

-- =============================================
-- Comment 相關 Stored Procedure
-- =============================================

-- sp_add_comment: 新增留言
-- 參數：p_user_id, p_post_id, p_content
-- 說明：將新留言資料插入 comment 表
CREATE PROCEDURE sp_add_comment(
    IN p_user_id BIGINT,
    IN p_post_id BIGINT,
    IN p_content TEXT
)
BEGIN
    DECLARE new_comment_id BIGINT;
INSERT INTO comment (user_id, post_id, content)
VALUES (p_user_id, p_post_id, p_content);
SET new_comment_id = LAST_INSERT_ID();
SELECT c.comment_id, c.post_id, c.user_id, u.user_name, c.content, c.created_at
FROM comment c
         JOIN user u ON c.user_id = u.user_id
WHERE c.comment_id = new_comment_id;
END$$

-- sp_get_comments_by_post_id: 取得發文的所有留言
-- 參數：p_post_id
-- 說明：按時間正序列出指定發文的所有留言
CREATE PROCEDURE sp_get_comments_by_post_id(
    IN p_post_id BIGINT
)
BEGIN
SELECT c.comment_id, c.post_id, c.user_id, u.user_name, c.content, c.created_at
FROM comment c
         JOIN user u ON c.user_id = u.user_id
WHERE c.post_id = p_post_id
ORDER BY c.created_at ASC;
END$$

-- sp_delete_comment: 刪除留言
-- 參數：p_comment_id, p_user_id
-- 說明：刪除指定留言（需驗證是否為留言者）
CREATE PROCEDURE sp_delete_comment(
    IN p_comment_id BIGINT,
    IN p_user_id BIGINT
)
BEGIN
DELETE FROM comment
WHERE comment_id = p_comment_id AND user_id = p_user_id;
END$$
