import { createApp } from 'vue'
import Vant from 'vant';
import './style.css'
import App from './App.vue'
import store from './store'
import router from '@/router';

// 创建vue实例
const app = createApp(App);
// 挂载pinia
app.use(store)
//
app.use(router)
app.use(Vant);
// Lazyload 指令需要单独进行注册
app.use(vant.Lazyload);
// 挂载实例
app.mount('#app');
