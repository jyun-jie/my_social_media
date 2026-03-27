# 社群媒體平台 - 前後端實作指南

本專案為三層式架構的社群媒體平台，後端使用 Spring Boot + MySQL，前端使用 Vue.js。

---

## 目錄概覽

```
my_social_media/
├── README.md                   # 本文件（整體說明）
├── backend/                    # 後端（Spring Boot）
│   ├── README.md               # 後端詳細說明
│   ├── pom.xml                 # Maven 依賴管理
│   └── src/
│       ├── main/java/project/  # Java 原始碼
│       └── DB/                 # 資料庫腳本
│           ├── schema.sql
│           ├── data.sql
│           ├── stored_procedures.sql
│           └── reset_dev_data.sql
└── frontend/                   # 前端（Vue.js）
    └── my_social_media/
        ├── README.md           # 前端詳細說明
        ├── package.json
        └── src/
            ├── api/            # API 服務層
            ├── components/     # 元件
            ├── views/          # 頁面
            └── router/         # 路由
```

---

## 功能總覽

### 一、帳號系統

| 功能 | 說明 | API |
|------|------|-----|
| 註冊 | 使用手機號碼與密碼註冊帳號，密碼經 BCrypt 雜湊後儲存 | `POST /api/auth/register` |
| 登入 | 以手機號碼與密碼登入，成功後回傳 JWT Token | `POST /api/auth/login` |
| Token 刷新 | 前端自動偵測 Token 即將過期，主動呼叫刷新 API 取得新 Token | `POST /api/auth/refresh` |
| 閒置自動登出 | 使用者閒置超過 15 分鐘無操作，系統自動登出並導回登入頁 | 前端計時器機制 |
| 多分頁同步登出 | 使用者在某一分頁登出時，其他分頁同步偵測並自動登出 | `localStorage` storage 事件監聽 |

### 二、發文功能

| 功能 | 說明 | API |
|------|------|-----|
| 新增發文 | 登入後可發布新文章，內容上限 1000 字 | `POST /api/posts` |
| 列出所有發文 | 取得全部發文列表，依時間倒序排列 | `GET /api/posts` |
| 查詢單篇發文 | 根據發文 ID 取得特定發文 | `GET /api/posts/{postId}` |
| 編輯發文 | 僅發文者本人可編輯自己的發文 | `PUT /api/posts/{postId}` |
| 刪除發文 | 僅發文者本人可刪除自己的發文 | `DELETE /api/posts/{postId}` |

### 三、留言功能

| 功能 | 說明 | API |
|------|------|-----|
| 新增留言 | 登入後可針對任一發文留言 | `POST /api/posts/{postId}/comments` |
| 查詢留言 | 取得某篇發文的所有留言，依時間正序排列 | `GET /api/posts/{postId}/comments` |
| 刪除留言 | 僅留言者本人可刪除自己的留言 | `DELETE /api/posts/{postId}/comments/{commentId}` |

### 四、前端互動

| 功能 | 說明 |
|------|------|
| 路由守衛 | 未登入者導向 `/login`，已登入者導向 `/home`，已登入時造訪登入/註冊頁自動轉跳 |
| 自動帶入 Token | 所有 API 請求自動附帶 JWT 於 Authorization Header |
| 401 自動處理 | Token 過期時自動清除本地 Token 並提示重新登入 |
| 響應式畫面 | 單欄式社群媒體排版，支援行動裝置螢幕寬度 |

---

## 系統架構

```
┌─────────────────────────────────────────────────────────┐
│                    前端 (Vue.js)                         │
│  ┌─────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐  │
│  │  Views  │──│Components│──│API Layer │──│  Router │  │
│  │ (頁面)   │  │  (元件)   │  │ (Axios)  │  │ (路由守衛)│  │
│  └─────────┘  └──────────┘  └────┬─────┘  └─────────┘  │
└──────────────────────────────────┼──────────────────────┘
                                   │ HTTP (REST API + JWT)
┌──────────────────────────────────┼──────────────────────┐
│                    後端 (Spring Boot)                    │
│  ┌───────────────┐  ┌──────────┴───┐  ┌──────────────┐  │
│  │  Controller   │──│   Service    │──│  Repository  │  │
│  │ (展示層)       │  │ (業務邏輯層)  │  │ (資料存取層)  │  │
│  │ AuthController│  │ UserService  │  │ UserRepo     │  │
│  │ PostController│  │ PostService  │  │ PostRepo     │  │
│  │ CommentCtrl   │  │ CommentSvc   │  │ CommentRepo  │  │
│  └───────────────┘  └──────────────┘  └──────┬───────┘  │
│                                               │          │
│  ┌────────────────────────────────────────────┴───────┐  │
│  │                    共用層                            │  │
│  │  JwtUtil · AuthUtil · Result · ValidationContext    │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────┬──────────────────────┘
                                   │ Stored Procedure Calls
┌──────────────────────────────────┴──────────────────────┐
│                     MySQL 資料庫                         │
│  ┌─────────┐  ┌──────────┐  ┌───────────────────────┐   │
│  │  user   │  │   post   │  │       comment         │   │
│  └─────────┘  └──────────┘  └───────────────────────┘   │
│                                                         │
│  Stored Procedures                                      │
│  ┌──────────────────┐  ┌─────────────────────────────┐  │
│  │ User Procedures  │  │ Post / Comment Procedures   │  │
│  │ sp_register_user │  │ sp_create_post              │  │
│  │ sp_login_user    │  │ sp_get_all_posts            │  │
│  │ sp_get_user_by_id│  │ sp_add_comment              │  │
│  └──────────────────┘  └─────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## 技術堆疊

| 層級 | 技術 | 版本 |
|------|------|------|
| 前端框架 | Vue.js | 3.5.x |
| 前端建置 | Vite | 7.3.x |
| 前端路由 | Vue Router | 4.x |
| 前端 HTTP | Axios | 1.13.x |
| 後端框架 | Spring Boot | 3.2.4 |
| 後端建置 | Maven | - |
| 後端安全 | Spring Security + JWT (jjwt) | 0.12.5 |
| 密碼雜湊 | BCrypt | - |
| 資料庫 | MySQL | 8.x |
| Java 版本 | JDK | 17+ |
| 測試框架 | JUnit 5 + Mockito | - |

---

## 資料庫設計

### 資料表

| 表名 | 說明 | 主要欄位 |
|------|------|----------|
| `user` | 使用者 | user_id (PK), user_name, phone_number, email, password, cover_image, biography, created_at |
| `post` | 發文 | post_id (PK), user_id (FK→user), content, image, created_at |
| `comment` | 留言 | comment_id (PK), user_id (FK→user), post_id (FK→post), content, created_at |

### 儲存程序（Stored Procedures）

| 名稱 | 用途 |
|------|------|
| `sp_register_user` | 註冊新使用者 |
| `sp_login_user` | 根據手機號碼查詢使用者（登入驗證） |
| `sp_get_user_by_id` | 根據 ID 取得使用者資料 |
| `sp_get_user_by_phone` | 根據手機號碼查詢使用者 |
| `sp_create_post` | 新增發文 |
| `sp_get_all_posts` | 取得所有發文 |
| `sp_get_post_by_id` | 根據 ID 取得發文 |
| `sp_update_post` | 更新發文 |
| `sp_delete_post` | 刪除發文 |
| `sp_add_comment` | 新增留言 |
| `sp_get_comments_by_post_id` | 取得某發文的所有留言 |
| `sp_delete_comment` | 刪除留言 |

### 資料庫腳本

| 檔案 | 用途 |
|------|------|
| `backend/src/DB/schema.sql` | DDL：建立資料表結構 |
| `backend/src/DB/data.sql` | DML：初始種子資料 |
| `backend/src/DB/stored_procedures.sql` | 建立所有儲存程序 |
| `backend/src/DB/reset_dev_data.sql` | 清空所有資料（開發用） |

---

## 安全性機制

| 項目 | 實作方式 |
|------|----------|
| 身份驗證 | JWT Token，有效期 15 分鐘，支援自動刷新 |
| 密碼儲存 | BCrypt 加鹽雜湊，不儲存明碼 |
| SQL Injection 防護 | 所有資料庫操作透過 Stored Procedure + 預編譯參數 |
| XSS 防護 | 前端使用 Vue 模板語法自動轉義，後端輸入驗證 |
| 跨表交易 | 使用 `@Transactional` 確保多表操作原子性 |
| Token 傳輸 | Authorization: Bearer {token}，前端 Axios 攔截器自動附加 |
| 閒置保護 | 15 分鐘無操作自動登出 |
| 多分頁一致性 | localStorage storage 事件同步登出狀態 |

---

## 設計模式

| 模式 | 應用場景 | 說明 |
|------|----------|------|
| 策略模式 (Strategy) | 輸入驗證 | `ValidationStrategy<T>` 介面，可擴充電話、密碼、內容等驗證規則 |
| 工廠模式 (Factory) | DTO 轉換 | `ResponseFactory<E, D>` 介面，將 Entity 轉為 Response DTO |

---

## API 端點總覽

| 方法 | 路徑 | 說明 | 需要登入 |
|------|------|------|----------|
| POST | `/api/auth/register` | 註冊 | ✗ |
| POST | `/api/auth/login` | 登入 | ✗ |
| POST | `/api/auth/refresh` | 刷新 Token | ✓ |
| POST | `/api/posts` | 新增發文 | ✓ |
| GET | `/api/posts` | 取得所有發文 | ✓ |
| GET | `/api/posts/{postId}` | 取得單篇發文 | ✓ |
| PUT | `/api/posts/{postId}` | 編輯發文 | ✓ |
| DELETE | `/api/posts/{postId}` | 刪除發文 | ✓ |
| POST | `/api/posts/{postId}/comments` | 新增留言 | ✓ |
| GET | `/api/posts/{postId}/comments` | 查詢留言 | ✓ |
| DELETE | `/api/posts/{postId}/comments/{commentId}` | 刪除留言 | ✓ |

---

## 快速開始

### 前置需求

| 工具 | 版本 |
|------|------|
| JDK | 17+ |
| Maven | - |
| MySQL | 8.x |
| Node.js | ^20.19.0 或 >=22.12.0 |
| npm | 最新版本 |

### 步驟

**1. 建立資料庫**

```sql
CREATE DATABASE my_social_media;
```

**2. 初始化資料庫**

```bash
mysql -u root -p my_social_media < backend/src/DB/schema.sql
mysql -u root -p my_social_media < backend/src/DB/reset_dev_data.sql
mysql -u root -p my_social_media < backend/src/DB/stored_procedures.sql
mysql -u root -p my_social_media < backend/src/DB/data.sql
```

**3. 啟動後端**

```bash
cd backend
mvn clean package
mvn spring-boot:run
```

後端啟動於 `http://localhost:8080`

**4. 啟動前端**

```bash
cd frontend/my_social_media
npm install
npm run dev
```

前端啟動於 `http://localhost:5173`

**5. 開始使用**

- 開啟瀏覽器前往 `http://localhost:5173`
- 註冊帳號 → 登入 → 發文 → 留言

---

## 詳細文件

| 文件 | 內容 |
|------|------|
| [backend/README.md](backend/README.md) | 後端架構、功能、建立與執行流程 |
| [frontend/README.md](frontend/README.md) | 前端架構、功能、建立與執行流程 |
