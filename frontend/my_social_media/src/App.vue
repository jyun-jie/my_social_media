<script setup>
import { ref, onMounted } from 'vue'
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import PostForm from './components/PostForm.vue'
import PostList from './components/PostList.vue'

const currentView = ref('login')
const isLoggedIn = ref(false)
const currentUserId = ref(null)
const editingPost = ref(null)
const postListRef = ref(null)

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

const handleLoginSuccess = (token) => {
  currentUserId.value = parseJwtToken(token)
  isLoggedIn.value = true
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
}

const handlePostCreated = () => {
  editingPost.value = null
  postListRef.value?.fetchPosts()
}

const handlePostUpdated = () => {
  editingPost.value = null
  postListRef.value?.fetchPosts()
}

const handleEditPost = (post) => {
  editingPost.value = post
  window.scrollTo({ top: 0, behavior: 'smooth' })
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
    }
  }
})
</script>

<template>
  <header>
    <div class="header-content">
      <h1>社群媒體平台</h1>
      <button v-if="isLoggedIn" @click="logout" class="logout-btn">登出</button>
    </div>
  </header>

  <main>
    <div v-if="isLoggedIn">
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
    </div>

    <Login
      v-else-if="currentView === 'login'"
      @login-success="handleLoginSuccess"
      @switch-to-register="currentView = 'register'"
    />

    <Register
      v-else
      @register-success="handleRegisterSuccess"
      @switch-to-login="currentView = 'login'"
    />
  </main>
</template>

<style scoped>
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  margin-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
}

.header-content h1 {
  color: var(--color-heading);
  font-size: 1.5rem;
}

.logout-btn {
  padding: 0.5rem 1rem;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.logout-btn:hover {
  background: #c0392b;
}
</style>
