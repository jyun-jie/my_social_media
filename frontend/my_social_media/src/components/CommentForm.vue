<script setup>
import { ref } from 'vue'
import { commentApi } from '../api/comment'

const props = defineProps({
  postId: {
    type: Number,
    required: true
  },
  currentUserId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['comment-added'])

const content = ref('')
const isLoading = ref(false)
const errorMessage = ref('')

const validateForm = () => {
  if (!content.value.trim()) {
    errorMessage.value = '留言內容不得為空'
    return false
  }
  if (content.value.length > 500) {
    errorMessage.value = '留言內容不得超過 500 字'
    return false
  }
  return true
}

const submit = async () => {
  errorMessage.value = ''

  if (!validateForm()) return

  isLoading.value = true

  try {
    const response = await commentApi.addComment(props.postId, { content: content.value })
    if (response.data.code === 200) {
      content.value = ''
      emit('comment-added', response.data.data)
    }
  } catch (error) {
    console.error('新增留言失敗:', error)
    errorMessage.value = error.response?.data?.message || '伺服器異常'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="comment-form">
    <div class="form-row">
      <input
        v-model="content"
        type="text"
        placeholder="寫下你的留言..."
        :disabled="isLoading"
        maxlength="500"
        @keyup.enter="submit"
      />
      <button @click="submit" :disabled="isLoading">
        {{ isLoading ? '...' : '送出' }}
      </button>
    </div>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
  </div>
</template>

<style scoped>
.comment-form {
  margin-top: 0.5rem;
}

.form-row {
  display: flex;
  gap: 0.5rem;
}

input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-background);
  color: var(--color-text);
  font-size: 0.9rem;
}

input:focus {
  outline: none;
  border-color: hsla(160, 100%, 37%, 1);
}

input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

button {
  padding: 0.5rem 1rem;
  background: hsla(160, 100%, 37%, 1);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  white-space: nowrap;
}

button:hover:not(:disabled) {
  background: hsla(160, 100%, 37%, 0.8);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  color: #e74c3c;
  font-size: 0.8rem;
  margin-top: 0.25rem;
}
</style>
