<script setup>
import { ref, onMounted } from 'vue'
import { commentApi } from '../api/comment'
import CommentForm from './CommentForm.vue'

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

const comments = ref([])
const commentCount = ref(0)
const isLoading = ref(false)
const showComments = ref(false)
const isCountLoaded = ref(false)

const fetchCommentCount = async () => {
  try {
    const response = await commentApi.getCommentsByPostId(props.postId)
    if (response.data.code === 200) {
      commentCount.value = response.data.data.length
    }
  } catch (error) {
    console.error('取得留言數量失敗:', error)
  }
}

const fetchComments = async () => {
  isLoading.value = true

  try {
    const response = await commentApi.getCommentsByPostId(props.postId)
    if (response.data.code === 200) {
      comments.value = response.data.data
      commentCount.value = response.data.data.length
    }
  } catch (error) {
    console.error('取得留言失敗:', error)
  } finally {
    isLoading.value = false
  }
}

const deleteComment = async (commentId) => {
  if (!confirm('確定要刪除此留言嗎？')) return

  try {
    const response = await commentApi.deleteComment(props.postId, commentId)
    if (response.data.code === 200) {
      comments.value = comments.value.filter(c => c.commentId !== commentId)
      commentCount.value = comments.value.length
    }
  } catch (error) {
    console.error('刪除留言失敗:', error)
    alert('刪除失敗：' + (error.response?.data?.message || '伺服器異常'))
  }
}

const handleCommentAdded = (newComment) => {
  comments.value.push(newComment)
  commentCount.value = comments.value.length
}

const toggleComments = () => {
  if (!showComments.value && comments.value.length === 0) {
    fetchComments()
  }
  showComments.value = !showComments.value
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-TW')
}

onMounted(() => {
  fetchCommentCount()
})

defineExpose({ fetchComments })
</script>

<template>
  <div class="comment-section">
    <button @click="toggleComments" class="toggle-btn">
      {{ showComments ? '隱藏留言' : `查看留言 (${commentCount})` }}
    </button>

    <div v-if="showComments" class="comments-container">
      <div v-if="isLoading" class="loading">載入中...</div>

      <div v-else>
        <div v-if="comments.length === 0" class="empty-message">
          目前沒有留言
        </div>

        <div v-else class="comments-list">
          <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
            <div class="comment-header">
              <span class="author">{{ comment.userName }}</span>
              <span class="date">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <button
              v-if="comment.userId === currentUserId"
              @click="deleteComment(comment.commentId)"
              class="delete-btn"
            >
              刪除
            </button>
          </div>
        </div>

        <CommentForm
          :post-id="postId"
          :current-user-id="currentUserId"
          @comment-added="handleCommentAdded"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.comment-section {
  margin-top: 0.5rem;
  border-top: 1px solid var(--color-border);
  padding-top: 0.5rem;
}

.toggle-btn {
  padding: 0.25rem 0.5rem;
  background: transparent;
  color: hsla(160, 100%, 37%, 1);
  border: none;
  cursor: pointer;
  font-size: 0.85rem;
  padding: 0;
}

.toggle-btn:hover {
  text-decoration: underline;
}

.comments-container {
  margin-top: 0.5rem;
}

.loading, .empty-message {
  text-align: center;
  padding: 0.5rem;
  color: var(--color-text);
  font-size: 0.9rem;
  opacity: 0.7;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.comment-item {
  padding: 0.5rem;
  background: var(--color-background);
  border-radius: 4px;
  border: 1px solid var(--color-border);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  margin-bottom: 0.25rem;
}

.author {
  font-weight: bold;
  color: hsla(160, 100%, 37%, 1);
}

.date {
  color: var(--color-text);
  opacity: 0.7;
}

.comment-content {
  font-size: 0.9rem;
  color: var(--color-text);
  margin-bottom: 0.25rem;
  white-space: pre-wrap;
}

.delete-btn {
  padding: 0.2rem 0.5rem;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.75rem;
}

.delete-btn:hover {
  background: #c0392b;
}
</style>
