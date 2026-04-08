import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 60000
})

// 请求拦截器 - 添加用户ID
api.interceptors.request.use(config => {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  if (user && user.id) {
    config.headers['X-User-Id'] = user.id
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
  updatePreference: (preference) => api.put('/user/preference', preference)
}

// 食材相关
export const foodApi = {
  recognize: (text, image, type) => api.post('/food/recognize', { text, image, type }),
  save: (food) => api.post('/food/save', food),
  list: () => api.get('/food/list'),
  detail: (id) => api.get('/food/detail', { params: { id } }),
  update: (food) => api.put('/food/update', food),
  delete: (id) => api.delete('/food/delete', { params: { id } }),
  nutrition: (foodId) => api.get('/food/nutrition', { params: { foodId } }),
  substitute: (foodId) => api.get('/food/substitute', { params: { foodId } })
}

// 食谱相关
export const recipeApi = {
  generate: (userId, foodIds, expectCount) => api.post(`/recipe/generate?userId=${userId}&foodIds=${foodIds.join(',')}&expectCount=${expectCount}`),
  save: (recipe) => api.post('/recipe/save', recipe),
  list: (userId) => api.get('/recipe/list', { params: { userId } }),
  detail: (id) => api.get('/recipe/detail', { params: { id } }),
  update: (recipe) => api.put('/recipe/update', recipe),
  delete: (id) => api.delete('/recipe/delete', { params: { id } }),
  collect: (id) => api.post('/recipe/collect', null, { params: { id } })
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
