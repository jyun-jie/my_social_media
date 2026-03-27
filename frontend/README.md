# Frontend - 說明與使用

功能概覽
- 使用 Vue 3 + Vite 架設，與後端 REST API 互動
- 登錄/註冊、發文、留言等介面，包含基本的狀態管理與路由

快速啟動
- src/api: 封裝呼叫後端 API
- src/router: 路由設定
-  views/components: UI 元件與頁面
-  .env 或 .env.local 內設定 API_BASE_URL

- 本地開發完成後，可打包部署
- 可撰寫端到端測試與單元測試，逐步覆蓋核心路徑

- 前端需對輸入做前端驗證，並在顯示使用者輸入時避免 XSS 風險
- 使用 API 之後端需驗證 JWT、避免跨站請求與未授權操作
### 從零開始建立 Frontend（From Zero - Frontend）
- 目的：提供前端快速啟動的步驟，與後端 API 對接，並說明專案結構與執行方式。
- 環境：Node.js 18+、npm、瀏覽器。
- 步驟：
  1) 安裝依賴
     - 在專案根目錄執行：`cd frontend && npm install`
  2) 設定 API 基底路徑
     - 建立 `.env.local`，設定 `VITE_API_BASE_URL=http://localhost:8080/api`
     或在專案中直接設定環境變數。
  3) 啟動開發伺服器
     - `npm run dev`
  4) 對接後端 API
     - 透過 UI 操作註冊、登入、發文、留言，前端會帶著 JWT 自動附在請求頭。

