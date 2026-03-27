-- 1) 永久清除資料以便每次開啟專案前都能從頭開始
-- 注意：請先確認已經在正確的資料庫中執行
-- 這份腳本會清空三張核心表：comment、post、user 及其資料依賴

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE comment;
TRUNCATE TABLE post;
TRUNCATE TABLE user;
SET FOREIGN_KEY_CHECKS = 1;

-- 執行完此腳本，資料庫已為空，可再執行 data.sql 進行種子資料注入
