import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 60000
})

// 请求拦截器 - 添加 JWT Token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API请求失败:', error)
    return Promise.reject(error)
  }
)

// 用户相关
export const userApi = {
  login: (username, password) => api.post(`/user/login?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`),
  register: (user) => api.post('/user/register', user),
  update: (user) => api.put('/user/update', user),
  getPreference: (userId) => api.get('/user/preference', { params: { userId } }),
  updatePreference: (preference) => api.put('/user/preference', preference),
  getCurrentUser: () => api.get('/user/me')
}

// 食材相关
export const foodApi = {
  recognize: (text, image, type, userId) => api.post('/food/recognize', { text, image, type, userId }),
  save: (food, userId) => api.post('/food/save', { ...food, userId }),
  list: (userId) => api.get('/food/list', { params: { userId } }),
  detail: (id, userId) => api.get('/food/detail', { params: { id, userId } }),
  update: (food, userId) => api.put('/food/update', { ...food, userId }),
  delete: (id, userId) => api.delete('/food/delete', { params: { id, userId } }),
  nutrition: (foodId) => api.get('/food/nutrition', { params: { foodId } }),
  substitute: (foodId) => api.get('/food/substitute', { params: { foodId } }),
  substituteByName: (foodName, foodType) => api.get('/food/substituteByName', { params: { foodName, foodType } })
}

// 食谱相关
export const recipeApi = {
  generate: (userId, foodIds, expectCount) => api.post(`/recipe/generate?userId=${userId}&foodIds=${foodIds.join(',')}&expectCount=${expectCount}`),
  save: (recipe, userId) => api.post('/recipe/save', { ...recipe, userId }),
  list: (userId) => api.get('/recipe/list', { params: { userId } }),
  detail: (id, userId) => api.get('/recipe/detail', { params: { id, userId } }),
  update: (recipe, userId) => api.put('/recipe/update', { ...recipe, userId }),
  delete: (id, userId) => api.delete('/recipe/delete', { params: { id, userId } }),
  collect: (id) => api.post('/recipe/collect', null, { params: { id } }),
  collectPersonal: (recipeId, userId) => api.post('/recipe/collectPersonal', null, { params: { recipeId, userId } }),
  collected: (userId) => api.get('/recipe/collected', { params: { userId } })
}

// 营养相关
export const nutritionApi = {
  analyze: (recipeId) => api.get('/nutrition/analyze', { params: { recipeId } }),
  save: (nutrition) => api.post('/nutrition/save', nutrition),
  detail: (id) => api.get('/nutrition/detail', { params: { id } }),
  update: (nutrition) => api.put('/nutrition/update', nutrition),
  delete: (id) => api.delete('/nutrition/delete', { params: { id } })
}

// 管理员相关
export const adminApi = {
  userList: () => api.get('/admin/user/list'),
  deleteUser: (id) => api.delete('/admin/user/delete', { params: { id } }),
  foodList: () => api.get('/admin/food/list'),
  deleteFood: (id) => api.delete('/admin/food/delete', { params: { id } }),
  recipeList: () => api.get('/admin/recipe/list'),
  deleteRecipe: (id) => api.delete('/admin/recipe/delete', { params: { id } })
}

export default api
