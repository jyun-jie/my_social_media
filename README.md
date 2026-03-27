# 社群媒體平台 - 前後端實作指南

本專案為三層式架構的社群媒體平台，後端使用 Spring Boot + MySQL，前端使用 Vue.js。

目錄概覽
- backend/    後端（Spring Boot）
- frontend/   前端（Vue.js）
- backend/src/DB/  資料庫相關腳本（DDL/DML/Stored Procedures）

架構與流程
- 使用者註冊／登入（JWT 驗證）
- 註冊成功後可發文與留言，發文與留言資料存放於資料庫
- 所有資料存取皆透過 Stored Procedures
- 後端分層：Controller -> Service -> Repository (Stored Procedures)；共用層有(JwtUtil、AuthUtil、Result)

需要的前提與工具
- JDK 17+ 與 Maven
- MySQL 8.x
- Node.js 與 npm（前端開發）
- 可用的資料庫使用者與權限

快速開始流程

資料庫種子與補充說明
- backend/src/DB/schema.sql: 建表與欄位
- backend/src/DB/data.sql: 初始資料（種子資料）
- backend/src/DB/stored_procedures.sql: 設定儲存程序
- backend/src/DB/reset_dev_data.sql: 每次啟動前清空資料的步驟

設計與測試
- 有單元測試（驗證策略）

流程圖
  使用者 -> 註冊/登入 -> 獲取 JWT -> 發文/留言 -> 伺服端透過 Stored Procedures 存取資料



