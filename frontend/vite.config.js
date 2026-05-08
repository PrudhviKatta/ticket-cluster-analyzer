import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Proxy /api calls to Spring Boot — avoids CORS issues in dev
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
