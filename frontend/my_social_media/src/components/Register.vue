<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api/auth'

const router = useRouter()

const userInfo = reactive({
  phoneNumber: '',
  password: '',
  email: '',
  userName: ''
})

const isLoading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const validateForm = () => {
  if (!userInfo.phoneNumber || !userInfo.password) {
    errorMessage.value = '手機號碼與密碼為必填欄位！'
    return false
  }

  const phoneRegex = /^09\d{8}$/
  if (!phoneRegex.test(userInfo.phoneNumber)) {
    errorMessage.value = '手機號碼格式錯誤，需為 09 開頭共 10 碼'
    return false
  }

  if (userInfo.password.length < 8) {
    errorMessage.value = '密碼長度至少 8 位'
    return false
  }

  return true
}

const submit = async () => {
  errorMessage.value = ''
  successMessage.value = ''

  if (!validateForm()) {
    return
  }

  isLoading.value = true

  try {
    const response = await authApi.register(userInfo)

    if (response.data.code === 200) {
      successMessage.value = '註冊成功！即將跳轉到登入頁面...'
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      errorMessage.value = response.data.message || '註冊失敗'
    }
  } catch (error) {
    console.error('API 請求失敗:', error)
    if (error.response && error.response.data) {
      errorMessage.value = error.response.data.message || '伺服器異常'
    } else {
      errorMessage.value = '無法連線到伺服器，請檢查後端是否啟動。'
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="register-container">
    <h2>註冊</h2>

    <div class="form-group">
      <label>手機號碼：</label>
      <input
        type="text"
        v-model="userInfo.phoneNumber"
        placeholder="例如: 0912345678"
        :disabled="isLoading"
      />
    </div>

    <div class="form-group">
      <label>密碼：</label>
      <input
        type="password"
        v-model="userInfo.password"
        placeholder="請設定密碼"
        :disabled="isLoading"
      />
    </div>

    <div class="form-group">
      <label>電子郵件：</label>
      <input
        type="email"
        v-model="userInfo.email"
        placeholder="example@mail.com"
        :disabled="isLoading"
      />
    </div>

    <div class="form-group">
      <label>使用者名稱：</label>
      <input
        type="text"
        v-model="userInfo.userName"
        placeholder="請輸入顯示名稱"
        :disabled="isLoading"
      />
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>

    <button @click="submit" :disabled="isLoading">
      {{ isLoading ? '註冊中...' : '提交註冊' }}
    </button>

    <p class="switch-text">
      已經有帳號？
      <router-link to="/login">登入</router-link>
    </p>
  </div>
</template>

<style scoped>
.register-container {
  width: 100%;
}

.register-container h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: var(--color-heading);
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--color-text);
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: 9999px;
  background: var(--color-background);
  color: var(--color-text);
  font-size: 1rem;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: hsla(160, 100%, 37%, 1);
}

.form-group input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.success-message {
  color: #27ae60;
  background: rgba(39, 174, 96, 0.1);
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

button {
  width: 100%;
  padding: 0.75rem;
  background: hsla(160, 100%, 37%, 1);
  color: white;
  border: none;
  border-radius: 9999px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s;
}

button:hover:not(:disabled) {
  background: hsla(160, 100%, 37%, 0.8);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.switch-text {
  text-align: center;
  margin-top: 1rem;
  color: var(--color-text);
}

.switch-text a {
  color: hsla(160, 100%, 37%, 1);
  text-decoration: none;
  font-weight: 500;
}

.switch-text a:hover {
  text-decoration: underline;
}
</style>