import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/register/index.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/layout/index.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/home/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('../views/product/list.vue'),
        meta: { title: '农产品' }
      },
      {
        path: 'product/publish',
        name: 'PublishProduct',
        component: () => import('../views/product/publish.vue'),
        meta: { title: '发布产品', requiresAuth: true, roles: ['farmer'] }
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('../views/product/detail.vue'),
        meta: { title: '产品详情' }
      },
      {
        path: 'supply-demand',
        name: 'SupplyDemand',
        component: () => import('../views/supply-demand/list.vue'),
        meta: { title: '供需信息' }
      },
      {
        path: 'supply-demand/publish',
        name: 'PublishSupplyDemand',
        component: () => import('../views/supply-demand/publish.vue'),
        meta: { title: '发布供需', requiresAuth: true }
      },
      {
        path: 'supply-demand/:id',
        name: 'SupplyDemandDetail',
        component: () => import('../views/supply-demand/detail.vue'),
        meta: { title: '供需详情' }
      },
      {
        path: 'market',
        name: 'Market',
        component: () => import('../views/market/index.vue'),
        meta: { title: '市场行情' }
      },
      {
        path: 'news',
        name: 'News',
        component: () => import('../views/news/list.vue'),
        meta: { title: '资讯' }
      },
      {
        path: 'news/:id',
        name: 'NewsDetail',
        component: () => import('../views/news/detail.vue'),
        meta: { title: '资讯详情' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/user/profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'my-products',
        name: 'MyProducts',
        component: () => import('../views/user/my-products.vue'),
        meta: { title: '我的产品', requiresAuth: true, roles: ['farmer'] }
      },
      {
        path: 'my-supply-demand',
        name: 'MySupplyDemand',
        component: () => import('../views/user/my-supply-demand.vue'),
        meta: { title: '我的供需', requiresAuth: true }
      },
      {
        path: 'my-comments',
        name: 'MyComments',
        component: () => import('../views/user/my-comments.vue'),
        meta: { title: '我的留言', requiresAuth: true }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('../views/user/favorites.vue'),
        meta: { title: '我的收藏', requiresAuth: true }
      },
      {
        path: 'help',
        name: 'Help',
        component: () => import('../views/help/index.vue'),
        meta: { title: '帮助中心' }
      }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/layout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, roles: ['admin'] },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/dashboard.vue'),
        meta: { title: '管理后台', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/users.vue'),
        meta: { title: '用户管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('../views/admin/products.vue'),
        meta: { title: '产品管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: 'news',
        name: 'AdminNews',
        component: () => import('../views/admin/news.vue'),
        meta: { title: '资讯管理', requiresAuth: true, roles: ['admin'] }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/home'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 农贸通` : '农贸通'
  
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  
  if (to.meta.requiresAuth) {
    if (!token) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
    
    if (to.meta.roles && to.meta.roles.length > 0) {
      if (!to.meta.roles.includes(role)) {
        next('/home')
        return
      }
    }
  }
  
  if ((to.path === '/login' || to.path === '/register') && token) {
    next('/home')
    return
  }
  
  next()
})

export default router
