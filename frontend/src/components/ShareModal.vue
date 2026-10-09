<template>
  <el-dialog v-model="visible" title="分享" width="400px" :before-close="handleClose">
    <div class="share-content">
      <!-- 分享卡片预览 -->
      <div class="share-card-preview">
        <div class="share-card">
          <div class="share-card-header">
            <img :src="shareInfo.coverImage" :alt="shareInfo.title" class="share-card-image">
            <div class="share-card-content">
              <h3 class="share-card-title">{{ shareInfo.title }}</h3>
              <p class="share-card-desc">{{ shareInfo.description }}</p>
            </div>
          </div>
          <div class="share-card-footer">
            <span class="share-card-source">非遗知识科普平台</span>
            <span class="share-card-time">{{ shareTime }}</span>
          </div>
        </div>
      </div>

      <!-- 分享方式 -->
      <div class="share-methods">
        <div class="share-method-item" @click="copyLink">
          <el-icon><Link /></el-icon>
          <span>复制链接</span>
        </div>
        <div class="share-method-item" @click="copyText">
          <el-icon><DocumentCopy /></el-icon>
          <span>复制文案</span>
        </div>
        <div class="share-method-item" @click="shareToWeChat">
          <el-icon><ChatDotRound /></el-icon>
          <span>分享到微信</span>
        </div>
        <div class="share-method-item" @click="shareToWeibo">
          <el-icon><Share /></el-icon>
          <span>分享到微博</span>
        </div>
      </div>
    </div>

    <!-- 预设文案 -->
    <div class="share-preset-text">
      <h4>分享文案：</h4>
      <el-input
        v-model="shareText"
        type="textarea"
        :rows="3"
        readonly
        placeholder="点击上方按钮复制文案"
      />
    </div>

    <!-- 操作按钮 -->
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="copyAll">一键复制</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Link, DocumentCopy, ChatDotRound, Share } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  shareInfo: {
    type: Object,
    default: () => ({
      title: '',
      description: '',
      coverImage: '',
      url: ''
    })
  }
})

const emit = defineEmits(['update:modelValue', 'close'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const shareText = ref('')

// 计算分享时间
const shareTime = computed(() => {
  return new Date().toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
})

// 生成分享文案
const generateShareText = () => {
  return `发现了一个很棒的非遗文化项目"${props.shareInfo.title}"，一起来了解中国传统文化的魅力吧！\n${props.shareInfo.description}\n${window.location.origin}${props.shareInfo.url}`
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  emit('close')
}

// 复制链接
const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(window.location.origin + props.shareInfo.url)
    ElMessage.success('链接已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败，请手动复制')
  }
}

// 复制文案
const copyText = async () => {
  shareText.value = generateShareText()
  try {
    await navigator.clipboard.writeText(shareText.value)
    ElMessage.success('文案已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败，请手动复制')
  }
}

// 一键复制
const copyAll = async () => {
  shareText.value = generateShareText()
  try {
    await navigator.clipboard.writeText(shareText.value)
    ElMessage.success('分享内容已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败，请手动复制')
  }
}

// 分享到微信
const shareToWeChat = () => {
  ElMessage.info('请使用微信扫码分享')
  // 这里可以接入微信分享SDK
}

// 分享到微博
const shareToWeibo = () => {
  const shareText = generateShareText()
  const shareUrl = encodeURIComponent(window.location.origin + props.shareInfo.url)
  const weiboUrl = `https://service.weibo.com/share/share.php?url=${shareUrl}&title=${encodeURIComponent(shareText)}`
  window.open(weiboUrl, '_blank')
}

// 组件挂载时生成文案
if (props.modelValue) {
  shareText.value = generateShareText()
}
</script>

<style scoped>
.share-content {
  margin-bottom: 20px;
}

.share-card-preview {
  margin-bottom: 20px;
}

.share-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.share-card-header {
  display: flex;
  padding: 16px;
}

.share-card-image {
  width: 80px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 12px;
}

.share-card-content {
  flex: 1;
}

.share-card-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--gq-primary);
}

.share-card-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.share-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
  border-top: 1px solid #eee;
}

.share-card-source {
  font-size: 12px;
  color: #999;
}

.share-card-time {
  font-size: 12px;
  color: #999;
}

.share-methods {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.share-method-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.share-method-item:hover {
  background: #f0f2f5;
  border-color: var(--gq-primary);
}

.share-method-item .el-icon {
  font-size: 24px;
  margin-bottom: 8px;
  color: var(--gq-primary);
}

.share-method-item span {
  font-size: 14px;
  color: #333;
}

.share-preset-text {
  margin-top: 20px;
}

.share-preset-text h4 {
  margin-bottom: 12px;
  color: #333;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>