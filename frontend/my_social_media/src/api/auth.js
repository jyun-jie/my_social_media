import api from './index'

export const authApi = {
  login(credentials) {
    return api.post('/auth/login', credentials)
  },
  register(userData) {
    return api.post('/auth/register', userData)
  },
  refresh() {
    return api.post('/auth/refresh')
  }
}
