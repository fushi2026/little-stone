import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import App from './App.vue'
import { router } from './router/index.ts'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)
const pinia = createPinia()

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

//pinia持久化插件
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

//路由
app.use(router)

app.use(ElementPlus)

app.mount('#app')
