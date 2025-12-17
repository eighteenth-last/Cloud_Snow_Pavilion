import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../views/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'store',
        name: 'Store',
        component: () => import('../views/Store.vue'),
        meta: { title: '门店管理' }
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('../views/Product.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('../views/Category.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('../views/Order.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'staff',
        name: 'Staff',
        component: () => import('../views/Staff.vue'),
        meta: { title: '员工管理' }
      },
      {
        path: 'ingredient',
        name: 'Ingredient',
        component: () => import('../views/Ingredient.vue'),
        meta: { title: '原料管理' }
      },
      {
        path: 'coupon',
        name: 'Coupon',
        component: () => import('../views/Coupon.vue'),
        meta: { title: '优惠券管理' }
      },
      {
        path: 'carousel',
        name: 'Carousel',
        component: () => import('../views/Carousel.vue'),
        meta: { title: '轮播图管理' }
      },
      {
        path: 'analysis',
        name: 'Analysis',
        component: () => import('../views/Analysis.vue'),
        meta: { title: '收入分析' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
