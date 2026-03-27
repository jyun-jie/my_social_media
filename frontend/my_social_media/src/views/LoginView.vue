<script setup>
import Login from '../components/Login.vue'
</script>

<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <h2>社群媒體平台</h2>
        <p>與朋友分享你的生活</p>
      </div>
      <Login />
    </div>
  </div>
</template>

<style scoped>
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
</style>
