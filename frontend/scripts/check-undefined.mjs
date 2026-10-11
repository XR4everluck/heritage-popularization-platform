/**
 * SFC 健康扫描（开发辅助脚本，不参与构建）：
 * 1. <script setup> 使用了 Vue API（computed/watch/…）却未从 vue 导入
 * 2. <template> 引用的顶层标识符在脚本中既未声明也未导入（排除 v-for/v-slot 局部变量与常见全局）
 */
import { readFileSync, readdirSync, statSync } from 'fs'
import { join } from 'path'

const VUE_APIS = ['computed', 'watch', 'reactive', 'nextTick', 'onMounted', 'onBeforeUnmount',
  'onUnmounted', 'watchEffect', 'toRefs', 'provide', 'inject', 'shallowRef', 'markRaw',
  'onMounted', 'onActivated', 'onDeactivated', 'onErrorCaptured']
const TEMPLATE_GLOBALS = new Set(['true', 'false', 'null', 'undefined', 'Math', 'Date', 'JSON',
  'String', 'Number', 'Boolean', 'Array', 'Object', 'console', 'window', 'document',
  '$event', '$attrs', '$slots', '$emit', '$refs', '$props', 'Infinity', 'NaN'])

function walk(dir, out = []) {
  for (const f of readdirSync(dir)) {
    const p = join(dir, f)
    const s = statSync(p)
    if (s.isDirectory()) walk(p, out)
    else if (f.endsWith('.vue')) out.push(p)
  }
  return out
}

const files = [...walk('src/views'), ...walk('src/components'), ...walk('src/layout')]
let issues = 0

for (const file of files) {
  const src = readFileSync(file, 'utf8')
  const sm = src.match(/<script setup>([\s\S]*?)<\/script>/)
  if (!sm) continue
  const code = sm[1]
  const tm = src.match(/<template>([\s\S]*)<\/template>/)
  const template = tm ? tm[1] : ''

  const names = new Set()
  for (const im of code.matchAll(/import\s+{([^}]+)}\s+from/g)) {
    im[1].split(',').forEach(x => { const n = x.trim().split(/\s+as\s+/).pop().trim(); if (n) names.add(n) })
  }
  for (const im of code.matchAll(/import\s+(\w+)\s+from/g)) names.add(im[1])
  for (const d of code.matchAll(/(?:function|const|let|var)\s+([A-Za-z_$][\w$]*)/g)) names.add(d[1])
  for (const d of code.matchAll(/,\s*([A-Za-z_$][\w$]*)\s*(?=[,}=])/g)) names.add(d[1])

  // 1. Vue API 未导入
  for (const api of VUE_APIS) {
    const re = new RegExp('(?<![\\w$.])' + api + '\\s*\\(')
    if (re.test(code) && !names.has(api)) {
      console.log(`${file}: [script] 使用了 ${api}() 但未从 vue 导入`)
      issues++
    }
  }

  // 2. 模板引用未声明标识符
  if (template) {
    // 收集模板局部变量：v-for / v-slot
    const locals = new Set()
    for (const v of template.matchAll(/v-for\s*=\s*"[^"]*\s+(?:in|of)\s+/gi)) { /* 先占位 */ }
    for (const v of template.matchAll(/v-for\s*=\s*"(\w+)\s*(?:,\s*(\w+))?\s+in\s+/g)) {
      locals.add(v[1]); if (v[2]) locals.add(v[2])
    }
    for (const v of template.matchAll(/v-slot:\w+="{\s*(\w+)/g)) locals.add(v[1])
    for (const v of template.matchAll(/#default="\{\s*(\w+)/g)) locals.add(v[1])

    // 提取模板中的标识符（插值与指令表达式）
    const exprs = []
    for (const m of template.matchAll(/{{([^}]*)}}/g)) exprs.push(m[1])
    for (const m of template.matchAll(/(?:v-[\w:.-]+|:[\w.-]+|@[\w.]+)\s*=\s*"([^"]*)"/g)) exprs.push(m[1])

    const seen = new Set()
    for (const expr of exprs) {
      for (const t of expr.matchAll(/(?<![\w$.'"-])([A-Za-z_$][\w$]*)/g)) {
        const id = t[1]
        if (names.has(id) || locals.has(id) || TEMPLATE_GLOBALS.has(id) || seen.has(id)) continue
        seen.add(id)
        console.log(`${file}: [template] 标识符 \`${id}\` 未在脚本中声明或导入`)
        issues++
      }
    }
  }
}
console.log(`扫描完成：${files.length} 个 SFC，发现 ${issues} 处可疑引用`)
