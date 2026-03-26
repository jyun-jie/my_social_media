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
    <h3>所有發文</h3>

    <div v-if="isLoading" class="loading">載入中...</div>

    <div v-else-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div v-else-if="posts.length === 0" class="empty-message">
      目前沒有發文
    </div>

    <div v-else class="posts">
      <div v-for="post in posts" :key="post.postId" class="post-card">
        <div class="post-header">
          <span class="author">{{ post.userName }}</span>
          <span class="date">{{ formatDate(post.createdAt) }}</span>
        </div>

        <div class="post-content">
          {{ post.content }}
        </div>

        <div v-if="post.userId === currentUserId" class="post-actions">
          <button @click="emit('edit-post', post)" class="edit-btn">編輯</button>
          <button @click="deletePost(post.postId)" class="delete-btn">刪除</button>
        </div>

        <CommentList
          :post-id="post.postId"
          :current-user-id="currentUserId"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.post-list {
  margin-top: 1.5rem;
}

.post-list h3 {
  color: var(--color-heading);
  margin-bottom: 1rem;
}

.loading, .empty-message {
  text-align: center;
  padding: 2rem;
  color: var(--color-text);
}

.error-message {
  color: #e74c3c;
  background: #fdf2f2;
  padding: 0.75rem;
  border-radius: 4px;
}

.posts {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.post-card {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1rem;
  background: var(--color-background-soft);
}

.post-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.75rem;
  font-size: 0.9rem;
}

.author {
  font-weight: bold;
  color: hsla(160, 100%, 37%, 1);
}

.date {
  color: var(--color-text);
  opacity: 0.7;
}

.post-content {
  color: var(--color-text);
  line-height: 1.6;
  white-space: pre-wrap;
  margin-bottom: 0.75rem;
}

.post-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.edit-btn {
  padding: 0.4rem 0.8rem;
  background: hsla(160, 100%, 37%, 1);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

.edit-btn:hover {
  background: hsla(160, 100%, 37%, 0.8);
}

.delete-btn {
  padding: 0.4rem 0.8rem;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

.delete-btn:hover {
  background: #c0392b;
}
</style>
