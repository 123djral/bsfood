import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('./views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('./views/Home.vue')
  },
  {
    path: '/food',
    name: 'Food',
    component: () => import('./views/Food.vue')
  },
  {
    path: '/recipe',
    name: 'Recipe',
    component: () => import('./views/Recipe.vue')
  },
  {
    path: '/nutrition',
    name: 'Nutrition',
    component: () => import('./views/Nutrition.vue')
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('./views/User.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('./views/Admin.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  if (!to.meta.public && !user) {
    next('/login')
  } else {
    next()
  }
})

export default router
