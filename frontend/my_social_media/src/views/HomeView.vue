<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import PostForm from '../components/PostForm.vue'
import PostList from '../components/PostList.vue'

const router = useRouter()
const currentUserId = ref(null)
const editingPost = ref(null)
const postListRef = ref(null)

// 自動登出計時器
const TIMEOUT_DURATION = 15 * 60 * 1000 // 15 分鐘
const REFRESH_THRESHOLD = 5 * 60 * 1000 // 剩餘 5 分鐘時刷新
const REFRESH_CHECK_INTERVAL = 60 * 1000 // 每分鐘檢查
let timeoutId = null
let refreshCheckId = null
let isRefreshing = false

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

import { authApi } from '../api/auth'

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

const resetTimeout = () => {
  if (timeoutId) clearTimeout(timeoutId)
  timeoutId = setTimeout(() => {
    alert('閒置過久，已自動登出')
    logout()
  }, TIMEOUT_DURATION)
}

const activityEvents = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click']

const handleUserActivity = () => {
  resetTimeout()
}

const startListening = () => {
  activityEvents.forEach(event => {
    document.addEventListener(event, handleUserActivity, { passive: true })
  })
  refreshCheckId = setInterval(refreshToken, REFRESH_CHECK_INTERVAL)
}

const stopListening = () => {
  activityEvents.forEach(event => {
    document.removeEventListener(event, handleUserActivity)
  })
  if (timeoutId) clearTimeout(timeoutId)
  if (refreshCheckId) clearInterval(refreshCheckId)
}

const logout = () => {
  localStorage.removeItem('token')
  stopListening()
  router.push('/login')
}

// 監聽其他分頁的登出事件
const handleStorageChange = (event) => {
  if (event.key === 'token' && event.newValue === null) {
    // 其他分頁刪除了 token
    alert('您的帳號已在其他分頁登出')
    router.push('/login')
  }
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
    currentUserId.value = parseJwtToken(token)
    startListening()
    resetTimeout()
    // 監聽 localStorage 變化（多分頁同步）
    window.addEventListener('storage', handleStorageChange)
  }
})

onUnmounted(() => {
  stopListening()
  window.removeEventListener('storage', handleStorageChange)
})
</script>

<template>
  <div class="home-container">
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
  </div>
</template>

<style scoped>
.home-container {
  min-height: 100vh;
  background: var(--color-background);
}

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

.main-content {
  max-width: 700px;
  margin: 0 auto;
  background: var(--color-background);
  border-left: 1px solid var(--color-border);
  border-right: 1px solid var(--color-border);
  min-height: calc(100vh - 60px);
}

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
