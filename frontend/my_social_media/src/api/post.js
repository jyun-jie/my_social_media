import api from './index'

export const postApi = {
  getAllPosts() {
    return api.get('/posts')
  },
  getPostById(postId) {
    return api.get(`/posts/${postId}`)
  },
  createPost(data) {
    return api.post('/posts', data)
  },
  updatePost(postId, data) {
    return api.put(`/posts/${postId}`, data)
  },
  deletePost(postId) {
    return api.delete(`/posts/${postId}`)
  }
}
