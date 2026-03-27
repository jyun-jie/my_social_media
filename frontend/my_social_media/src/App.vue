<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import PostForm from './components/PostForm.vue'
import PostList from './components/PostList.vue'
import { authApi } from './api/auth'

const currentView = ref('login')
const isLoggedIn = ref(false)
const currentUserId = ref(null)
const editingPost = ref(null)
const postListRef = ref(null)

// 自動登出計時器
const TIMEOUT_DURATION = 15 * 60 * 1000 // 15 分鐘（毫秒）
const REFRESH_THRESHOLD = 5 * 60 * 1000 // 剩餘 5 分鐘時刷新
const REFRESH_CHECK_INTERVAL = 60 * 1000 // 每分鐘檢查一次是否需要刷新
let timeoutId = null
let refreshCheckId = null
let isRefreshing = false // 防止重複刷新

const parseJwtToken = (token) => {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const payload = JSON.parse(window.atob(base64))
    return parseInt(payload.sub)
  } catch (e) {
    return null
  }
}

// 檢查 Token 是否即將過期
const isTokenExpiringSoon = () => {
  const token = localStorage.getItem('token')
  if (!token) return false
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const payload = JSON.parse(window.atob(base64))
    const exp = payload.exp * 1000
    const now = Date.now()
    return (exp - now) < REFRESH_THRESHOLD
  } catch (e) {
    return false
  }
}

// 刷新 Token（只在即將過期時執行）
const refreshToken = async () => {
  if (isRefreshing || !isTokenExpiringSoon()) return
  isRefreshing = true
  try {
    const response = await authApi.refresh()
    if (response.data.code === 200) {
      localStorage.setItem('token', response.data.message)
      currentUserId.value = parseJwtToken(response.data.message)
    }
  } catch (error) {
    console.error('Token 刷新失敗:', error)
  } finally {
    isRefreshing = false
  }
}

// 重設自動登出計時器
const resetTimeout = () => {
  if (timeoutId) {
    clearTimeout(timeoutId)
  }
  if (isLoggedIn.value) {
    timeoutId = setTimeout(() => {
      alert('閒置過久，已自動登出')
      logout()
    }, TIMEOUT_DURATION)
  }
}

// 監聽使用者活動
const activityEvents = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click']

const handleUserActivity = () => {
  resetTimeout()
}

const startListening = () => {
  activityEvents.forEach(event => {
    document.addEventListener(event, handleUserActivity, { passive: true })
  })
  // 定期檢查是否需要刷新 Token
  refreshCheckId = setInterval(refreshToken, REFRESH_CHECK_INTERVAL)
}

const stopListening = () => {
  activityEvents.forEach(event => {
    document.removeEventListener(event, handleUserActivity)
  })
  if (timeoutId) {
    clearTimeout(timeoutId)
    timeoutId = null
  }
  if (refreshCheckId) {
    clearInterval(refreshCheckId)
    refreshCheckId = null
  }
}

const handleLoginSuccess = (token) => {
  currentUserId.value = parseJwtToken(token)
  isLoggedIn.value = true
  resetTimeout()
}

const handleRegisterSuccess = () => {
  currentView.value = 'login'
}

const logout = () => {
  localStorage.removeItem('token')
  isLoggedIn.value = false
  currentUserId.value = null
  currentView.value = 'login'
  editingPost.value = null
  stopListening()
}

const handlePostCreated = () => {
  editingPost.value = null
  postListRef.value?.fetchPosts()
  resetTimeout()
}

const handlePostUpdated = () => {
  editingPost.value = null
  postListRef.value?.fetchPosts()
  resetTimeout()
}

const handleEditPost = (post) => {
  editingPost.value = post
  window.scrollTo({ top: 0, behavior: 'smooth' })
  resetTimeout()
}

const cancelEdit = () => {
  editingPost.value = null
}

onMounted(() => {
  const token = localStorage.getItem('token')
  if (token) {
    const userId = parseJwtToken(token)
    if (userId) {
      currentUserId.value = userId
      isLoggedIn.value = true
      startListening()
      resetTimeout()
    }
  }
})

onUnmounted(() => {
  stopListening()
})

// 監聽登入狀態變化
watch(isLoggedIn, (newValue) => {
  if (newValue) {
    startListening()
    resetTimeout()
  } else {
    stopListening()
  }
})
</script>

<template>
  <div class="app-container">
    <!-- 已登入 -->
    <template v-if="isLoggedIn">
      <!-- 頂部導航欄 -->
      <header class="header">
        <div class="header-left">
          <h1>社群媒體平台</h1>
        </div>
        <div class="header-right">
          <button @click="logout" class="logout-btn">登出</button>
        </div>
      </header>

      <!-- 主要內容區 -->
      <main class="main-content">
        <PostForm
          :edit-post="editingPost"
          @post-created="handlePostCreated"
          @post-updated="handlePostUpdated"
          @cancel-edit="cancelEdit"
        />

        <PostList
          ref="postListRef"
          :current-user-id="currentUserId"
          @edit-post="handleEditPost"
        />
      </main>
    </template>

    <!-- 未登入：顯示登入/註冊表單 -->
    <div v-else class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <h2>社群媒體平台</h2>
          <p>與朋友分享你的生活</p>
        </div>

        <Login
          v-if="currentView === 'login'"
          @login-success="handleLoginSuccess"
          @switch-to-register="currentView = 'register'"
        />

        <Register
          v-else
          @register-success="handleRegisterSuccess"
          @switch-to-login="currentView = 'login'"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  background: var(--color-background);
}

/* 頂部導航欄 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  border-bottom: 1px solid var(--color-border);
  background: var(--color-background);
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-left h1 {
  font-size: 1.25rem;
  color: var(--color-heading);
  margin: 0;
}

.logout-btn {
  padding: 0.5rem 1.5rem;
  background: transparent;
  color: #e74c3c;
  border: 1px solid #e74c3c;
  border-radius: 9999px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s;
}

.logout-btn:hover {
  background: #e74c3c;
  color: white;
}

/* 主要內容區 */
.main-content {
  max-width: 700px;
  margin: 0 auto;
  background: var(--color-background);
  border-left: 1px solid var(--color-border);
  border-right: 1px solid var(--color-border);
  min-height: calc(100vh - 60px);
}

/* 登入/註冊頁面 */
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 2rem;
}

.auth-card {
  width: 100%;
  max-width: 400px;
  background: var(--color-background-soft);
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.auth-header {
  text-align: center;
  margin-bottom: 1.5rem;
}

.auth-header h2 {
  color: var(--color-heading);
  margin: 0 0 0.5rem 0;
  font-size: 1.5rem;
}

.auth-header p {
  color: var(--color-text);
  opacity: 0.7;
  margin: 0;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .main-content {
    border-left: none;
    border-right: none;
  }

  .header {
    padding: 1rem;
  }
}
</style>
