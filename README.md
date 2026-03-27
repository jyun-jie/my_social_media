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

- 依照前後端Readme.md 建置完成
- 透過 UI 操作註冊、登入、發文、留言，前端會帶著 JWT 自動附在請求頭(需先註冊)。
範本 手機: 0911222333 密碼: 12345678 email :xxxxx@gmail.com 使用者名稱:chen 


功能總覽

帳號系統：註冊、登入、Token 刷新、閒置自動登出、多分頁同步登出

發文功能：新增 / 查詢 / 編輯 / 刪除

留言功能：新增 / 查詢 / 刪除

前端互動：路由守衛、自動帶入 Token、401 處理

<br>
安全性機制

JWT、BCrypt、Stored Procedure 防 SQL Injection、XSS 防護、交易管理、閒置保護、多分頁同步



流程圖
  使用者 -> 註冊/登入 -> 獲取 JWT -> 發文/留言 -> 伺服端透過 Stored Procedures 存取資料

請先git clone 之後再依照前後端內的Readme.md 執行前置流程
