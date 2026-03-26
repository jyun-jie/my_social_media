<script setup>
import { ref } from 'vue'
import Login from './components/Login.vue'
import Register from './components/Register.vue'

const currentView = ref('login')
const isLoggedIn = ref(false)

const handleLoginSuccess = (token) => {
  console.log('登入成功，Token:', token)
  isLoggedIn.value = true
}

const handleRegisterSuccess = () => {
  currentView.value = 'login'
}

const logout = () => {
  localStorage.removeItem('token')
  isLoggedIn.value = false
  currentView.value = 'login'
}
</script>

<template>
  <header>
    <div class="header-content">
      <h1>社群媒體平台</h1>
      <button v-if="isLoggedIn" @click="logout" class="logout-btn">登出</button>
    </div>
  </header>

  <main>
    <div v-if="isLoggedIn" class="logged-in-message">
      <p>歡迎回來！您已成功登入。</p>
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

.logged-in-message {
  text-align: center;
  padding: 2rem;
  background: hsla(160, 100%, 37%, 0.1);
  border-radius: 8px;
}

.logged-in-message p {
  color: hsla(160, 100%, 37%, 1);
  font-size: 1.2rem;
}
</style>
