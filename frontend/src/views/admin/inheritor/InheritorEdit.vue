<template>
  <div class="inheritor-edit-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑传承人' : '添加传承人' }}</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="max-height: 600px; overflow-y: auto"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入传承人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入传承人标题/头衔" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="关联非遗" prop="heritageId">
          <el-select v-model="form.heritageId" placeholder="请选择关联的非遗项目" style="width: 100%">
            <el-option
              v-for="item in heritageOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="个人简介" prop="introduction">
          <el-input
            v-model="form.introduction"
            type="textarea"
            :rows="4"
            placeholder="请输入个人简介"
          />
        </el-form-item>

        <el-form-item label="传承经历" prop="experience">
          <el-input
            v-model="form.experience"
            type="textarea"
            :rows="4"
            placeholder="请输入传承经历"
          />
        </el-form-item>

        <el-form-item label="荣誉成就" prop="achievements">
          <el-input
            v-model="form.achievements"
            type="textarea"
            :rows="4"
            placeholder="请输入荣誉成就"
          />
        </el-form-item>

        <el-form-item label="标签" prop="tags">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>

        <el-form-item label="头像" prop="avatar">
          <div class="avatar-upload">
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="form.avatar" :src="form.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">建议尺寸：200x200px，大小不超过2MB</div>
          </div>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" :max="999" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="form-footer">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { inheritorApi, heritageApi } from '../../api/admin'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const isEdit = computed(() => !!route.params.id)

// 表单数据
const form = reactive({
  id: null,
  name: '',
  title: '',
  heritageId: null,
  heritageName: '',
  introduction: '',
  experience: '',
  achievements: '',
  tags: '',
  avatar: '',
  status: 1,
  sort: 0
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入传承人姓名', trigger: 'blur' }],
  title: [{ required: true, message: '请输入传承人标题', trigger: 'blur' }],
  heritageId: [{ required: true, message: '请选择关联的非遗项目', trigger: 'change' }]
}

// 非遗选项
const heritageOptions = ref([])

// 上传相关
const uploadUrl = computed(() => `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/api/admin/file/upload`)
const uploadHeaders = computed(() => {
  const userStore = useUserStore()
  return {
    Authorization: `Bearer ${userStore.token}`
  }
})

// 加载传承人详情
async function loadDetail() {
  if (!isEdit.value) return
  
  try {
    const res = await inheritorApi.get(route.params.id)
    Object.assign(form, res.data)
  } catch (error) {
    ElMessage.error('加载传承人详情失败')
    router.push('/admin/inheritor')
  }
}

// 加载非遗选项
async function loadHeritageOptions() {
  try {
    const res = await heritageApi.page({ pageSize: 1000 })
    heritageOptions.value = res.data.records
  } catch (error) {
    ElMessage.error('加载非遗选项失败')
  }
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    
    if (isEdit.value) {
      await inheritorApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await inheritorApi.add(form)
      ElMessage.success('添加成功')
    }
    
    router.push('/admin/inheritor')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败')
    }
  }
}

// 返回
function goBack() {
  router.push('/admin/inheritor')
}

// 头像上传成功
function handleAvatarSuccess(res) {
  if (res.code === 200) {
    form.avatar = res.data.url
  } else {
    ElMessage.error('上传失败')
  }
}

// 头像上传前校验
function beforeAvatarUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

// 用户store
function useUserStore() {
  return { token: localStorage.getItem('token') }
}

onMounted(() => {
  loadHeritageOptions()
  loadDetail()
})
</script>

<style scoped>
.inheritor-edit-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>