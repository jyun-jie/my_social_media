# AGENTS.md

本文件為代理（Agent）協作指南，提供編譯、程式碼風格等規範。

---

## 1. Build / Lint 指令

### Development

```bash
# 啟動開發伺服器
npm run dev

# 建置生產版本
npm run build

# 預覽生產版本
npm run preview
```

### Lint / Code Quality

```bash
# ESLint 檢查（若已安裝）
npm run lint

# 自動修復 ESLint 問題（若已安裝）
npm run lint:fix
```

---

## 2. 專案結構

```
frontend/my_social_media/
├── public/                    # 靜態資源
├── src/
│   ├── api/                   # API 服務層
│   │   ├── index.js           # Axios 實例與攔截器
│   │   ├── auth.js            # 認證相關 API
│   │   ├── post.js            # 發文相關 API
│   │   └── comment.js         # 留言相關 API
│   ├── assets/                # 靜態資源（CSS、圖片）
│   ├── components/            # 可重用元件
│   │   ├── common/            # 通用元件（Button、Modal 等）
│   │   └── icons/             # 圖示元件
│   ├── composables/           # Vue 3 組合式函數
│   ├── router/                # Vue Router 設定
│   │   └── index.js
│   ├── stores/                # Pinia 狀態管理
│   │   ├── auth.js            # 認證狀態
│   │   ├── post.js            # 發文狀態
│   │   └── comment.js         # 留言狀態
│   ├── views/                 # 頁面元件
│   │   ├── Login.vue
│   │   ├── Register.vue
│   │   ├── Home.vue
│   │   ├── PostDetail.vue
│   │   └── Profile.vue
│   ├── App.vue                # 根元件
│   └── main.js                # 應用程式入口
├── index.html                 # HTML 入口
├── vite.config.js             # Vite 設定
├── package.json               # 專案依賴
└── AGENTS.md                  # 本文件
```

---

## 3. 技術堆疊

| 元件 | 版本/說明 |
|------|----------|
| Vue.js | 3.5.30 |
| Vite | 7.3.1 |
| Vue Router | 4.x（待安裝） |
| Pinia | 2.x（待安裝） |
| Axios | 1.13.6 |
| CSS | Tailwind CSS 或原生 CSS（待決定） |

---

## 4. 程式碼風格指南

### 元件命名慣例

| 類型 | 命名規則 | 範例 |
|------|---------|------|
| 元件檔案 | PascalCase.vue | `UserProfile.vue` |
| 元件名稱 | PascalCase | `UserProfile` |
| 方法 | camelCase | `handleSubmit()` |
| 變數 | camelCase | `userName` |
| 常數 | UPPER_SNAKE_CASE | `MAX_LENGTH` |
| CSS class | kebab-case | `user-profile` |
| Pinia Store | camelCase + Store | `useAuthStore` |
| API 模組 | camelCase | `authApi` |

### Vue 元件結構順序

```vue
<script setup>
// 1. Imports
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

// 2. Props 定義
const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

// 3. Emits 定義
const emit = defineEmits(['update', 'delete'])

// 4. Store 使用
const authStore = useAuthStore()

// 5. 響應式狀態
const userName = ref('')
const isLoading = ref(false)

// 6. 計算屬性
const fullName = computed(() => `${user.firstName} ${user.lastName}`)

// 7. 方法
const handleSubmit = () => {
  // 實作邏輯
}

// 8. 生命週期鉤子
onMounted(() => {
  // 初始化邏輯
})
</script>

<template>
  <!-- HTML 模板 -->
</template>

<style scoped>
/* CSS 樣式 */
</style>
```

### API 服務層規範

```javascript
// src/api/index.js
import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 請求攔截器
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 回應攔截器
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token 過期處理
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
```

### Pinia Store 範例

```javascript
// src/stores/auth.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)
  const isAuthenticated = computed(() => !!token.value)

  // Actions
  const login = async (credentials) => {
    try {
      const response = await authApi.login(credentials)
      token.value = response.data.token
      user.value = response.data.user
      localStorage.setItem('token', token.value)
      return { success: true }
    } catch (error) {
      return { success: false, error: error.message }
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  return {
    user,
    token,
    isAuthenticated,
    login,
    logout
  }
})
```

### 路由設定範例

```javascript
// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { guest: true }
  },
  {
    path: '/post/:id',
    name: 'PostDetail',
    component: () => import('@/views/PostDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守衛
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.guest && authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router
```

### 元件 Props 定義範例

```vue
<script setup>
const props = defineProps({
  // 必要 prop
  userId: {
    type: Number,
    required: true
  },
  // 可選 prop 帶預設值
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  // 物件 prop
  user: {
    type: Object,
    default: () => ({})
  }
})
</script>
```

### 事件定義範例

```vue
<script setup>
const emit = defineEmits(['update', 'delete', 'submit'])

const handleClick = () => {
  emit('update', { id: 1, name: 'test' })
}
</script>
```

---

## 5. 安全性規範

- JWT Token 儲存在 `localStorage`，並透過 Axios 攔截器自動附加
- 敏感資料（密碼等）不應在前端明文儲存
- API 請求使用 HTTPS（生產環境）
- 防止 XSS：使用 Vue 的模板語法自動轉義，避免 `v-html` 直接渲染使用者輸入
- 防止 CSRF：使用 Token 驗證

---

## 6. 環境變數

建立 `.env` 檔案在專案根目錄：

```env
# API 基礎路徑
VITE_API_BASE_URL=http://localhost:8080/api

# 應用程式標題
VITE_APP_TITLE=社群媒體平台
```

---

## 7. 環境需求

| 工具 | 版本要求 |
|------|----------|
| Node.js | ^20.19.0 或 >=22.12.0 |
| npm | 最新版本 |

---

## 8. 注意事項

- 使用 Composition API（`<script setup>`）撰寫元件
- 元件檔案使用 PascalCase 命名（例如 `UserProfile.vue`）
- API 路徑與後端一致，使用 `/api/` 前綴
- 路由使用懶加載（`component: () => import()`）
- 狀態管理使用 Pinia，避免全域變數
- CSS 優先使用 Scoped Style
- 圖片和靜態資源放在 `public/` 或 `src/assets/`
- 實作完給我commit 內文 但不要自行git commit
