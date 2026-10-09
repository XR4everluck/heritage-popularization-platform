<template>
  <div class="inheritor-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>传承人管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>添加传承人
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="关键词">
            <el-input v-model="searchForm.keyword" placeholder="姓名/标题" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格区域 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="heritageName" label="关联非遗" min-width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button type="success" link @click="handleToggleStatus(row)">
              <el-icon><Switch /></el-icon>{{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加传承人' : '编辑传承人'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-height: 500px; overflow-y: auto"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入传承人姓名" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入传承人标题/头衔" />
        </el-form-item>
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
            :rows="3"
            placeholder="请输入个人简介"
          />
        </el-form-item>
        <el-form-item label="传承经历" prop="experience">
          <el-input
            v-model="form.experience"
            type="textarea"
            :rows="3"
            placeholder="请输入传承经历"
          />
        </el-form-item>
        <el-form-item label="荣誉成就" prop="achievements">
          <el-input
            v-model="form.achievements"
            type="textarea"
            :rows="3"
            placeholder="请输入荣誉成就"
          />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
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
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Switch, Delete } from '@element-plus/icons-vue'
import { inheritorApi, heritageApi } from '../../api/admin'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: null
})

// 表格数据
const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表单数据
const form = reactive({
  id: null,
  name: '',
  title: '',
  heritageId: null,
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

// 加载传承人列表
async function loadList() {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
      status: searchForm.status
    }
    const res = await inheritorApi.page(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('加载列表失败')
  } finally {
    loading.value = false
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

// 搜索
function handleSearch() {
  page.value = 1
  loadList()
}

// 重置
function handleReset() {
  searchForm.keyword = ''
  searchForm.status = null
  page.value = 1
  loadList()
}

// 添加
function handleAdd() {
  dialogType.value = 'add'
  dialogVisible.value = true
  resetForm()
}

// 编辑
function handleEdit(row) {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

// 删除
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除该传承人吗？', '提示', { type: 'warning' })
    await inheritorApi.remove(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 切换状态
async function handleToggleStatus(row) {
  try {
    await inheritorApi.updateStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success('状态更新成功')
    loadList()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

// 提交
async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    if (dialogType.value === 'add') {
      await inheritorApi.add(form)
      ElMessage.success('添加成功')
    } else {
      await inheritorApi.update(form)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败')
    }
  }
}

// 重置表单
function resetForm() {
  form.id = null
  form.name = ''
  form.title = ''
  form.heritageId = null
  form.introduction = ''
  form.experience = ''
  form.achievements = ''
  form.tags = ''
  form.avatar = ''
  form.status = 1
  form.sort = 0
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

// 分页
function handleSizeChange(val) {
  pageSize.value = val
  loadList()
}

function handleCurrentChange(val) {
  page.value = val
  loadList()
}

// 用户store
function useUserStore() {
  return { token: localStorage.getItem('token') }
}

onMounted(() => {
  loadList()
  loadHeritageOptions()
})
</script>

<style scoped>
.inheritor-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-area {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  object-fit: cover;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>