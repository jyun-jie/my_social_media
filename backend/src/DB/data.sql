-- =============================================
-- DML: 測試資料
-- 用途：提供開發和測試用的初始資料
-- =============================================

-- 注意：密碼已使用 BCrypt 加密
-- 明文密碼: password123
-- BCrypt 雜湊值: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- 插入測試使用者
INSERT INTO user (user_name, phone_number, email, password, biography) VALUES
('張三', '0912345678', 'zhang@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '大家好，我是張三'),
('李四', '0923456789', 'li@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '熱愛程式設計的李四'),
('王五', '0934567890', 'wang@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '攝影愛好者王五');

-- 插入測試發文
INSERT INTO post (user_id, content) VALUES
(1, '今天天氣真好！適合出去走走。'),
(1, '學習 Spring Boot 的第一天，加油！'),
(2, '分享一個好用的開發工具：VS Code'),
(3, '週末去爬山，風景美極了！');

-- 插入測試留言
INSERT INTO comment (user_id, post_id, content) VALUES
(2, 1, '是啊，天氣真的很棒！'),
(3, 1, '我也想出去走走'),
(1, 3, 'VS Code 確實很好用'),
(3, 4, '看起來好美，下次一起去！'),
(2, 4, '照片拍得真好');
