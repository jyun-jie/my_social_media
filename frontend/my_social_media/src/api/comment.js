import api from './index'

export const commentApi = {
  getCommentsByPostId(postId) {
    return api.get(`/posts/${postId}/comments`)
  },
  addComment(postId, data) {
    return api.post(`/posts/${postId}/comments`, data)
  },
  deleteComment(postId, commentId) {
    return api.delete(`/posts/${postId}/comments/${commentId}`)
  }
}
