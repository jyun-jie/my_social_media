<script setup>
import { ref, onMounted } from 'vue'
import { postApi } from '../api/post'
import CommentList from './CommentList.vue'

const props = defineProps({
  currentUserId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['edit-post'])

const posts = ref([])
const isLoading = ref(false)
const errorMessage = ref('')

const fetchPosts = async () => {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await postApi.getAllPosts()
    if (response.data.code === 200) {
      posts.value = response.data.data
    }
  } catch (error) {
    console.error('取得發文失敗:', error)
    errorMessage.value = '無法載入發文列表'
  } finally {
    isLoading.value = false
  }
}

const deletePost = async (postId) => {
  if (!confirm('確定要刪除此發文嗎？')) return

  try {
    const response = await postApi.deletePost(postId)
    if (response.data.code === 200) {
      posts.value = posts.value.filter(p => p.postId !== postId)
    }
  } catch (error) {
    console.error('刪除發文失敗:', error)
    alert('刪除失敗：' + (error.response?.data?.message || '伺服器異常'))
  }
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-TW')
}

onMounted(() => {
  fetchPosts()
})

defineExpose({ fetchPosts })
</script>

<template>
  <div class="post-list">
    <div v-if="isLoading" class="loading">載入中...</div>

    <div v-else-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div v-else-if="posts.length === 0" class="empty-message">
      目前沒有發文，快來發布第一篇吧！
    </div>

    <div v-else class="posts">
      <div v-for="post in posts" :key="post.postId" class="post-card">
        <div class="post-header">
          <div class="post-author">
            <div class="avatar">{{ post.userName?.charAt(0) }}</div>
            <span class="author-name">{{ post.userName }}</span>
          </div>
          <span class="post-date">{{ formatDate(post.createdAt) }}</span>
        </div>

        <div class="post-content">
          {{ post.content }}
        </div>

        <div class="post-footer">
          <CommentList
            :post-id="post.postId"
            :current-user-id="currentUserId"
          />

          <div v-if="post.userId === currentUserId" class="post-actions">
            <button @click="emit('edit-post', post)" class="edit-btn">編輯</button>
            <button @click="deletePost(post.postId)" class="delete-btn">刪除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.post-list {
  /* No additional styles needed */
}

.loading, .empty-message {
  text-align: center;
  padding: 3rem 1rem;
  color: var(--color-text);
  opacity: 0.6;
}

.error-message {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
  padding: 1rem;
  border-radius: 0.5rem;
  margin: 1rem;
}

.posts {
  display: flex;
  flex-direction: column;
}

.post-card {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
  transition: background 0.2s;
}

.post-card:hover {
  background: var(--color-background-soft);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: hsla(160, 100%, 37%, 0.2);
  color: hsla(160, 100%, 37%, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1rem;
}

.author-name {
  font-weight: bold;
  color: var(--color-heading);
  font-size: 1rem;
}

.post-date {
  font-size: 0.85rem;
  color: var(--color-text);
  opacity: 0.5;
}

.post-content {
  color: var(--color-text);
  line-height: 1.6;
  white-space: pre-wrap;
  font-size: 1rem;
  margin-bottom: 0.75rem;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.post-actions {
  display: flex;
  gap: 0.5rem;
}

.edit-btn, .delete-btn {
  padding: 0.35rem 0.75rem;
  background: transparent;
  border-radius: 9999px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.2s;
}

.edit-btn {
  color: hsla(160, 100%, 37%, 1);
  border: 1px solid hsla(160, 100%, 37%, 1);
}

.edit-btn:hover {
  background: hsla(160, 100%, 37%, 0.1);
}

.delete-btn {
  color: #e74c3c;
  border: 1px solid #e74c3c;
}

.delete-btn:hover {
  background: rgba(231, 76, 60, 0.1);
}
</style>
