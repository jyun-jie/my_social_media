# Frontend - 說明與使用

功能概覽
- 使用 Vue 3 + Vite 架設，與後端 REST API 互動
- 登錄/註冊、發文、留言等介面，包含基本的狀態管理與路由

架構
- src/api: 封裝呼叫後端 API
- src/router: 路由設定
-  views/components: UI 元件與頁面


### 從零開始建立 Frontend（From Zero - Frontend）
- 目的：提供前端快速啟動的步驟，與後端 API 對接，並說明專案結構與執行方式。
- 環境：Node.js 18+、npm、瀏覽器。
- 步驟：
  1) 安裝依賴
     - 在專案根目錄執行：`cd my_social/media && npm install`
  2) 啟動開發伺服器
     - `npm run dev`
  3) 對接後端 API
     - 透過 UI 操作註冊、登入、發文、留言，前端會帶著 JWT 自動附在請求頭(需先註冊)。
範本 手機: 0911222333 密碼: 12345678 email :xxxxx@gmail.com 使用者名稱:chen 

