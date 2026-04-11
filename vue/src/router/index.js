import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/user/Login.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/user/Register.vue'),
      meta: { title: '注册' }
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/home/Home.vue'),
      meta: { 
        title: '首页',
        requiresAuth: true
      }
    },
    {
      path: '/newsDetail',
      name: 'newsDetail',
      component: () => import('../views/news/NewsDetail.vue'),
      meta: { 
        title: '新闻详情',
        requiresAuth: true
      }
    },
    {
      path: '/message',
      name: 'message',
      component: () => import('../views/message/MessageBoard.vue'),
      meta: { 
        title: '留言板',
        requiresAuth: true
      }
    },
     {
      path: '/newsCategory',
      name: 'newsCategory',
      component: () => import('../views/news/NewsCategory.vue'),
      meta: { 
        title: '新闻分类',
        requiresAuth: true
      }
    },
      {
      path: '/search',
      name: 'search',
      component: () => import('../views/news/SearchResults.vue'),
      meta: { 
        title: '搜索结果',
        requiresAuth: true
      }
    },
    {
      path: '/user',
      name: 'user',
      component: () => import('../views/user/Layout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: 'my-news',
          name: 'myNews',
          component: () => import('../views/user/MyNews.vue'),
          meta: { title: '我的新闻' }
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/user/Profile.vue'),
          meta: { title: '个人中心' }
        },
        {
          path: 'favorites',
          name: 'favorites',
          component: () => import('../views/user/Favorites.vue'),
          meta: { title: '我的收藏' }
        },
        {
          path: 'history',
          name: 'history',
          component: () => import('../views/user/History.vue'),
          meta: { title: '浏览历史' }
        },
        {
          path: 'likes',
          name: 'likes',
          component: () => import('../views/user/Likes.vue'),
          meta: { title: '点赞记录' }
        },
        {
          path: 'preferences',
          name: 'preferences',
          component: () => import('../views/user/Preferences.vue'),
          meta: { title: '偏好设置' }
        },
        {
          path: 'feedback',
          name: 'feedback',
          component: () => import('../views/user/Feedback.vue'),
          meta: { title: '问题反馈' }
        }
      ]
    },
    {
      path: '/404',
      name: '404',
      component: () => import('../views/404.vue')
    },
    {
      path: '/:pathMatch(.*)',
      redirect: '/404'
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 新闻推荐系统`
  }
  
  // 需要登录的页面检查token
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next('/login')
      return
    }
  }
  
  next()
})

export default router 