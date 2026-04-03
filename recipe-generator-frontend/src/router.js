import { createRouter, createWebHistory } from 'vue-router'

const routes = [
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

export default router