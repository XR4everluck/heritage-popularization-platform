import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Vite 配置：Vue3 单页应用
// 后端接口地址通过 .env.development 的 VITE_API_BASE_URL 配置（axios 统一封装使用）
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    // 代理备用方案：如需同源开发可将 request.js 的 baseURL 改为 '/api-prefix' 并在此转发
    proxy: {
      '/files': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
