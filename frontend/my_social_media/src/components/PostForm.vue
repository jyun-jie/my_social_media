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
      placeholder="分享你的想法..."
      rows="4"
      :disabled="isLoading"
      maxlength="1000"
    ></textarea>

    <div class="char-count">{{ content.length }} / 1000</div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div class="form-actions">
      <button @click="submit" :disabled="isLoading" class="submit-btn">
        {{ isLoading ? '處理中...' : (editPost ? '更新' : '發布') }}
      </button>
      <button v-if="editPost" @click="cancel" :disabled="isLoading" class="cancel-btn">
        取消
      </button>
    </div>
  </div>
</template>

<style scoped>
.post-form {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1rem;
  background: var(--color-background-soft);
}

.post-form h3 {
  color: var(--color-heading);
  margin-bottom: 0.75rem;
}

textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-background);
  color: var(--color-text);
  font-size: 1rem;
  resize: vertical;
  font-family: inherit;
}

textarea:focus {
  outline: none;
  border-color: hsla(160, 100%, 37%, 1);
}

textarea:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.char-count {
  text-align: right;
  font-size: 0.8rem;
  color: var(--color-text);
  opacity: 0.7;
  margin-top: 0.25rem;
}

.error-message {
  color: #e74c3c;
  background: #fdf2f2;
  padding: 0.75rem;
  border-radius: 4px;
  margin-top: 0.5rem;
  font-size: 0.9rem;
}

.form-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.75rem;
}

.submit-btn {
  padding: 0.5rem 1.5rem;
  background: hsla(160, 100%, 37%, 1);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.submit-btn:hover:not(:disabled) {
  background: hsla(160, 100%, 37%, 0.8);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.cancel-btn {
  padding: 0.5rem 1.5rem;
  background: #95a5a6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.cancel-btn:hover:not(:disabled) {
  background: #7f8c8d;
}
</style>
