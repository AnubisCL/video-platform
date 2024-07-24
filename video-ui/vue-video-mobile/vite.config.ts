import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import * as path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
  resolve: {
    //设置别名
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  plugins: [vue()],
  server: {
    port: 7076, //启动端口
    hmr: {
      host: '127.0.0.1',
      port: 7076
    },
    // 设置 https 代理
    proxy: {
      '/api': {
        target: '127.0.0.1:7077',
        changeOrigin: true,
        logLevel: 'debug',
        rewrite: (path: string) => path.replace(/^\/api/, '')
      }
    }
  }
});

