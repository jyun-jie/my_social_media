-- =============================================
-- Stored Procedure 定义
-- 用途：将数据库操作逻辑封装在数据库端
-- =============================================

-- 删除旧的 Procedure（如果存在）
DROP PROCEDURE IF EXISTS sp_register_user;
DROP PROCEDURE IF EXISTS sp_login_user;
DROP PROCEDURE IF EXISTS sp_get_user_by_id;
DROP PROCEDURE IF EXISTS sp_get_user_by_phone;
DROP PROCEDURE IF EXISTS sp_create_post;
DROP PROCEDURE IF EXISTS sp_get_all_posts;
DROP PROCEDURE IF EXISTS sp_get_post_by_id;
DROP PROCEDURE IF EXISTS sp_update_post;
DROP PROCEDURE IF EXISTS sp_delete_post;
DROP PROCEDURE IF EXISTS sp_add_comment;
DROP PROCEDURE IF EXISTS sp_get_comments_by_post_id;
DROP PROCEDURE IF EXISTS sp_delete_comment;

-- =============================================
-- User 相关 Stored Procedure
-- =============================================

-- sp_register_user: 注册新用户
-- 参数：p_user_name, p_phone_number, p_email, p_password
-- 说明：将新用户数据插入 user 表
CREATE PROCEDURE sp_register_user(
    IN p_user_name VARCHAR(50),
    IN p_phone_number VARCHAR(20),
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(255)
)
BEGIN
    INSERT INTO user (user_name, phone_number, email, password)
    VALUES (p_user_name, p_phone_number, p_email, p_password);
END;

-- sp_login_user: 登录验证
-- 参数：p_phone_number
-- 说明：根据手机号查询用户信息（密码验证在 Java 端进行）
CREATE PROCEDURE sp_login_user(
    IN p_phone_number VARCHAR(20)
)
BEGIN
    SELECT user_id, user_name, phone_number, email, password
    FROM user
    WHERE phone_number = p_phone_number;
END;

-- sp_get_user_by_id: 根据 ID 获取用户
-- 参数：p_user_id
-- 说明：查询指定用户的基本信息
CREATE PROCEDURE sp_get_user_by_id(
    IN p_user_id BIGINT
)
BEGIN
    SELECT user_id, user_name, phone_number, email, cover_image, biography, created_at
    FROM user
    WHERE user_id = p_user_id;
END;

-- sp_get_user_by_phone: 根据手机号获取用户
-- 参数：p_phone_number
-- 说明：检查手机号是否已注册
CREATE PROCEDURE sp_get_user_by_phone(
    IN p_phone_number VARCHAR(20)
)
BEGIN
    SELECT user_id, user_name, phone_number, email
    FROM user
    WHERE phone_number = p_phone_number;
END;

-- =============================================
-- Post 相关 Stored Procedure
-- =============================================

-- sp_create_post: 新增发文
-- 参数：p_user_id, p_content, p_image
-- 说明：将新发文数据插入 post 表
CREATE PROCEDURE sp_create_post(
    IN p_user_id BIGINT,
    IN p_content TEXT,
    IN p_image VARCHAR(255),
    OUT p_post_id BIGINT
)
BEGIN
    INSERT INTO post (user_id, content, image)
    VALUES (p_user_id, p_content, p_image);
    
    SET p_post_id = LAST_INSERT_ID();
    
    -- 返回新创建的发文信息
    SELECT p.post_id, p.user_id, u.user_name, p.content, p.image, p.created_at
    FROM post p
    JOIN user u ON p.user_id = u.user_id
    WHERE p.post_id = p_post_id;
END;

-- sp_get_all_posts: 获取所有发文
-- 说明：按时间倒序列出所有发文，包含发文者名称
CREATE PROCEDURE sp_get_all_posts()
BEGIN
    SELECT p.post_id, p.user_id, u.user_name, p.content, p.image, p.created_at
    FROM post p
    JOIN user u ON p.user_id = u.user_id
    ORDER BY p.created_at DESC;
END;

-- sp_get_post_by_id: 根据 ID 获取发文
-- 参数：p_post_id
-- 说明：查询指定发文的详细信息
CREATE PROCEDURE sp_get_post_by_id(
    IN p_post_id BIGINT
)
BEGIN
    SELECT p.post_id, p.user_id, u.user_name, p.content, p.image, p.created_at
    FROM post p
    JOIN user u ON p.user_id = u.user_id
    WHERE p.post_id = p_post_id;
END;

-- sp_update_post: 更新发文
-- 参数：p_post_id, p_user_id, p_content, p_image
-- 说明：更新指定发文的内容（需验证是否为发文者）
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
    
    -- 返回更新后的发文信息
    SELECT p.post_id, p.user_id, u.user_name, p.content, p.image, p.created_at
    FROM post p
    JOIN user u ON p.user_id = u.user_id
    WHERE p.post_id = p_post_id;
END;

-- sp_delete_post: 删除发文
-- 参数：p_post_id, p_user_id
-- 说明：删除指定发文（需验证是否为发文者）
CREATE PROCEDURE sp_delete_post(
    IN p_post_id BIGINT,
    IN p_user_id BIGINT
)
BEGIN
    DELETE FROM post
    WHERE post_id = p_post_id AND user_id = p_user_id;
END;

-- =============================================
-- Comment 相关 Stored Procedure
-- =============================================

-- sp_add_comment: 新增留言
-- 参数：p_user_id, p_post_id, p_content
-- 说明：将新留言数据插入 comment 表
CREATE PROCEDURE sp_add_comment(
    IN p_user_id BIGINT,
    IN p_post_id BIGINT,
    IN p_content TEXT,
    OUT p_comment_id BIGINT
)
BEGIN
    INSERT INTO comment (user_id, post_id, content)
    VALUES (p_user_id, p_post_id, p_content);
    
    SET p_comment_id = LAST_INSERT_ID();
    
    -- 返回新创建的留言信息
    SELECT c.comment_id, c.post_id, c.user_id, u.user_name, c.content, c.created_at
    FROM comment c
    JOIN user u ON c.user_id = u.user_id
    WHERE c.comment_id = p_comment_id;
END;

-- sp_get_comments_by_post_id: 获取发文的所有留言
-- 参数：p_post_id
-- 说明：按时间正序列出指定发文的所有留言
CREATE PROCEDURE sp_get_comments_by_post_id(
    IN p_post_id BIGINT
)
BEGIN
    SELECT c.comment_id, c.post_id, c.user_id, u.user_name, c.content, c.created_at
    FROM comment c
    JOIN user u ON c.user_id = u.user_id
    WHERE c.post_id = p_post_id
    ORDER BY c.created_at ASC;
END;

-- sp_delete_comment: 删除留言
-- 参数：p_comment_id, p_user_id
-- 说明：删除指定留言（需验证是否为留言者）
CREATE PROCEDURE sp_delete_comment(
    IN p_comment_id BIGINT,
    IN p_user_id BIGINT
)
BEGIN
    DELETE FROM comment
    WHERE comment_id = p_comment_id AND user_id = p_user_id;
END;
