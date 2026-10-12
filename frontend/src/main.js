import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
// Element Plus 中文本地化：确认框/分页/日期选择等组件文案显示中文
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import '@wangeditor/editor/dist/css/style.css'
import App from './App.vue'
import router from './router'
// v-lazy 图片懒加载指令（docs/11 第 4.1 节：仅本地资源、拦截外链、占位防 CLS）
import { lazy } from './directives/lazy'
import './styles/global.css'

const app = createApp(App)

// 全局注册 Element Plus（中文 locale）与图标
app.use(ElementPlus, { locale: zhCn })
app.use(createPinia())
app.use(router)
// 全局注册图片懒加载指令：<img v-lazy="本地资源URL" />
app.directive('lazy', lazy)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局错误兜底：渲染/事件错误不再静默清空组件树，保留堆栈便于排查
app.config.errorHandler = (err, instance, info) => {
  console.error(`[应用错误] ${info}:`, err)
}

app.mount('#app')
