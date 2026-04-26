import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:9999',
        changeOrigin: true,
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        }
      }
    }
  },
  build: {
    charset: 'utf8'
  }
})
