-- =============================================
-- DML: 测试数据
-- 用途：提供开发和测试用的初始数据
-- =============================================

-- 注意：密码已使用 BCrypt 加密
-- 明文密码: password123
-- BCrypt 哈希值: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- 插入测试用户
INSERT INTO user (user_name, phone_number, email, password, biography) VALUES
('张三', '0912345678', 'zhang@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '大家好，我是张三'),
('李四', '0923456789', 'li@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '热爱编程的李四'),
('王五', '0934567890', 'wang@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '摄影爱好者王五');

-- 插入测试发文
INSERT INTO post (user_id, content) VALUES
(1, '今天天气真好！适合出去走走。'),
(1, '学习 Spring Boot 的第一天，加油！'),
(2, '分享一个好用的开发工具：VS Code'),
(3, '周末去爬山，风景美极了！');

-- 插入测试留言
INSERT INTO comment (user_id, post_id, content) VALUES
(2, 1, '是啊，天气真的很棒！'),
(3, 1, '我也想出去走走'),
(1, 3, 'VS Code 确实很好用'),
(3, 4, '看起来好美，下次一起去！'),
(2, 4, '照片拍得真好');
