<script setup>
import { ref, watch } from 'vue'
import { postApi } from '../api/post'

const props = defineProps({
  editPost: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['post-created', 'post-updated', 'cancel-edit'])

const content = ref('')
const isLoading = ref(false)
const errorMessage = ref('')

watch(() => props.editPost, (newVal) => {
  if (newVal) {
    content.value = newVal.content
  } else {
    content.value = ''
  }
}, { immediate: true })

const validateForm = () => {
  if (!content.value.trim()) {
    errorMessage.value = '發文內容不得為空'
    return false
  }
  if (content.value.length > 1000) {
    errorMessage.value = '發文內容不得超過 1000 字'
    return false
  }
  return true
}

const submit = async () => {
  errorMessage.value = ''

  if (!validateForm()) return

  isLoading.value = true

  try {
    const data = { content: content.value }

    if (props.editPost) {
      const response = await postApi.updatePost(props.editPost.postId, data)
      if (response.data.code === 200) {
        emit('post-updated', response.data.data)
        content.value = ''
      }
    } else {
      const response = await postApi.createPost(data)
      if (response.data.code === 200) {
        emit('post-created', response.data.data)
        content.value = ''
      }
    }
  } catch (error) {
    console.error('操作失敗:', error)
    errorMessage.value = error.response?.data?.message || '伺服器異常'
  } finally {
    isLoading.value = false
  }
}

const cancel = () => {
  content.value = ''
  errorMessage.value = ''
  emit('cancel-edit')
}
</script>

<template>
  <div class="post-form">
    <h3>{{ editPost ? '編輯發文' : '新增發文' }}</h3>

    <textarea
      v-model="content"
      placeholder="有什麼新鮮事？"
      rows="3"
      :disabled="isLoading"
      maxlength="1000"
    ></textarea>

    <div class="form-footer">
      <div class="char-count">{{ content.length }} / 1000</div>
      <div class="form-actions">
        <button v-if="editPost" @click="cancel" :disabled="isLoading" class="cancel-btn">
          取消
        </button>
        <button @click="submit" :disabled="isLoading" class="submit-btn">
          {{ isLoading ? '處理中...' : (editPost ? '更新' : '發布') }}
        </button>
      </div>
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<style scoped>
.post-form {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

.post-form h3 {
  color: var(--color-heading);
  margin-bottom: 0.75rem;
  font-size: 1.1rem;
  display: none;
}

textarea {
  width: 100%;
  padding: 1rem;
  border: 1px solid var(--color-border);
  border-radius: 0.75rem;
  background: var(--color-background-soft);
  color: var(--color-text);
  font-size: 1rem;
  resize: none;
  font-family: inherit;
  box-sizing: border-box;
  line-height: 1.5;
}

textarea:focus {
  outline: none;
  border-color: hsla(160, 100%, 37%, 1);
}

textarea:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.75rem;
}

.char-count {
  font-size: 0.85rem;
  color: var(--color-text);
  opacity: 0.6;
}

.form-actions {
  display: flex;
  gap: 0.5rem;
}

.error-message {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
  padding: 0.75rem;
  border-radius: 0.5rem;
  margin-top: 0.75rem;
  font-size: 0.9rem;
}

.submit-btn {
  padding: 0.5rem 1.5rem;
  background: hsla(160, 100%, 37%, 1);
  color: white;
  border: none;
  border-radius: 9999px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: bold;
  transition: background 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background: hsla(160, 100%, 37%, 0.8);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.cancel-btn {
  padding: 0.5rem 1rem;
  background: transparent;
  color: var(--color-text);
  border: 1px solid var(--color-border);
  border-radius: 9999px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: all 0.3s;
}

.cancel-btn:hover:not(:disabled) {
  background: var(--color-background-soft);
  border-color: var(--color-text);
}
</style>
