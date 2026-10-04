<script setup>
/**
 * 通用上传组件：图片/视频上传，v-model 绑定文件 URL
 * 父组件用法：<FileUpload v-model="form.cover" type="image" />
 */
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { uploadFile } from '../api/file'

const props = defineProps({
  modelValue: { type: String, default: '' },
  type: { type: String, default: 'image' },
  width: { type: String, default: '178px' },
  height: { type: String, default: '120px' }
})
const emit = defineEmits(['update:modelValue'])

const uploading = ref(false)

const isVideo = computed(() => props.type === 'video')

/** 自定义上传：调后端接口，成功后把 URL 回传给父组件 */
async function customUpload({ file }) {
  uploading.value = true
  try {
    const res = await uploadFile(file, props.type)
    emit('update:modelValue', res.data)
    ElMessage.success('上传成功')
  } finally {
    uploading.value = false
  }
}

function beforeUpload(file) {
  if (isVideo.value && file.size > 500 * 1024 * 1024) {
    ElMessage.error('视频最大500MB')
    return false
  }
  if (!isVideo.value && file.size > 10 * 1024 * 1024) {
    ElMessage.error('图片最大10MB')
    return false
  }
  return true
}
</script>

<template>
  <div>
    <!-- 已有文件：预览 + 删除重传 -->
    <div v-if="modelValue" class="preview-box" :style="{ width, height }">
      <img v-if="!isVideo" :src="modelValue" class="preview-media" fit="cover" />
      <video v-else :src="modelValue" class="preview-media" controls />
      <div class="preview-mask">
        <el-icon @click="emit('update:modelValue', '')"><Delete /></el-icon>
      </div>
    </div>
    <!-- 上传入口 -->
    <el-upload
      v-else
      :show-file-list="false"
      :http-request="customUpload"
      :before-upload="beforeUpload"
      :accept="isVideo ? 'video/*' : 'image/*'"
    >
      <div v-loading="uploading" class="preview-box upload-entry" :style="{ width, height }">
        <el-icon><Plus /></el-icon>
        <span class="upload-text">{{ uploading ? '上传中...' : isVideo ? '上传视频' : '上传图片' }}</span>
      </div>
    </el-upload>
  </div>
</template>

<style scoped>
.preview-box {
  position: relative;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  overflow: hidden;
  background: #fafafa;
}
.preview-media {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.upload-entry {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
  cursor: pointer;
  gap: 4px;
}
.upload-text {
  font-size: 12px;
}
.preview-mask {
  position: absolute;
  inset: 0;
  display: none;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 22px;
  cursor: pointer;
}
.preview-box:hover .preview-mask {
  display: flex;
}
</style>
