# Backend - 說明與使用

功能概覽
- 註冊 / 登入 / 發文 / 留言，採用三層式架構
- 使用 Stored Procedure 存取資料庫，並實作交易控制

架構總覽
- Presentation: REST Controller (AuthController, PostController, CommentController)
- Business: Service 實作（UserServiceImpl、PostServiceImpl、CommentServiceImpl）
- Data: RepositoryCustom 呼叫儲存程序
- Common: ( Auth , Jwt , Result ）等共用元件

資料庫與初始化
- DDL: backend/src/DB/schema.sql
- Seed: backend/src/DB/data.sql
- Stored Procedures: backend/src/DB/stored_procedures.sql
- 重置資料（每次啟動前）: backend/src/DB/reset_dev_data.sql

啟動與執行
- 重置資料時，需先清空再注入資料，避免重複 insert
- 以交易機制保證跨表更新的一致性
- 透過 JWT 驗證與授權，保護 API 路徑
- 通常 seed 資料放在 data.sql，reset_dev_data.sql 用於開發/測試時的重置

### 從零開始建立 Backend（From Zero - Backend）
- 目的：提供一組可直接從空資料庫建立整套後端環境的步驟，包含資料庫初始化、種子資料、儲存程序、與專案啟動。
- 需求與環境：Java 17+、Maven、MySQL、Git、JDK、資料庫帳號與密碼。
- 步驟：
  1) 建立資料庫
     - 1. mysql -u root -p ;
     - 2. CREATE DATABASE my_social_media ; 
     - 3. CREATE USER admin@localhost IDENTIFIED BY '123456' ;
     - 4. GRANT ALL PRIVILEGES ON my_social_media.* TO 'admin'@'localhost' ;
     - 5. FLUSH PRIVILEGES ;
  2) 導入資料庫結構與預設資料
     - 導入 schema：`backend/src/DB/schema.sql`
     - 重置資料：`backend/src/DB/reset_dev_data.sql`
     - 注入初始種子資料：`backend/src/DB/data.sql`
     - 導入儲存程序：`backend/src/DB/stored_procedures.sql`
  3) 事務與交易
     - 確保在需要的場合使用 @Transactional 以確保多表變更的原子性。
  4) 編譯與啟動
     - 在專案根目錄執行：`mvn clean package`，完成後 `java -jar backend/target/<artifact>.jar`  或 `mvn spring-boot:run`。
  5) 驗證 API
     - 測試路由：`/api/auth/register`, `/api/auth/login`, `/api/posts` 等。
  6) 常見問題
     - 若資料庫連線/憑證有誤，請檢查 `application.properties` 的資料庫設定是否正確。
