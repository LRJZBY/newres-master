import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  // 新增：代理配置（核心修复）
  server: {
    port: 5173,        // 前端运行端口（保持不变）
    host: true,        // 允许局域网访问
    open: true,        // 启动后自动打开浏览器
    proxy: {
      // 将 /api 开头的请求转发到后端 8080 端口
      '/api': {
        target: 'http://localhost:8080',  // 后端服务地址
        changeOrigin: true,              // 开启跨域代理
        rewrite: (path) => path.replace(/^\/api/, '')  // 去掉 /api 前缀
      }
    }
  }
})