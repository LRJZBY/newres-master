import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

//引入Element-Plus到全局
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@/styles/variables.css'  // 引入全局变量
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 配置Element Plus主题
app.use(ElementPlus, {
  theme: {
    colors: {
      'primary': '#ff9966',
    }
  }
})

app.use(router)
app.mount('#app')
