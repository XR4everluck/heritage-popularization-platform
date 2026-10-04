<script setup>
/**
 * 轮播图管理：分页列表 + 新增/编辑（图片上传、跳转链接、排序、启用状态）+ 删除
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { bannerApi } from '../../api/admin'
import FileUpload from '../../components/FileUpload.vue'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 10 })

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const form = reactive({ id: null, image: '', linkUrl: '', title: '', sort: 1, status: 1 })
const rules = {
  image: [{ required: true, message: '请上传轮播图图片', trigger: 'change' }]
}

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await bannerApi.page(query)
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

function openAdd() {
  Object.assign(form, { id: null, image: '', linkUrl: '', title: '', sort: list.value.length + 1, status: 1 })
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    form.id ? await bannerApi.update({ ...form }) : await bannerApi.add({ ...form })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除轮播图【${row.title || row.id}】吗？`, '提示', { type: 'warning' })
  await bannerApi.remove(row.id)
  ElMessage.success('已删除')
  loadData()
}
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-button type="primary" plain @click="openAdd"><el-icon><Plus /></el-icon>&nbsp;新增轮播图</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column label="图片" width="180">
        <template #default="{ row }">
          <img :src="row.image" style="width: 150px; height: 70px; object-fit: cover; border-radius: 6px" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
      <el-table-column prop="linkUrl" label="跳转链接" min-width="140" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序号" width="90" align="center" />
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" plain @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" plain @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination v-model:current-page="query.page" :page-size="query.pageSize" :total="total"
                     layout="total, prev, pager, next" background @current-change="loadData" />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑轮播图' : '新增轮播图'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="图片" prop="image"><FileUpload v-model="form.image" type="image" width="300px" height="140px" /></el-form-item>
        <el-form-item label="标题"><el-input v-model="form.title" maxlength="100" /></el-form-item>
        <el-form-item label="跳转链接"><el-input v-model="form.linkUrl" placeholder="如 /heritage/1（前端路由）" /></el-form-item>
        <el-form-item label="排序号"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.toolbar {
  margin-bottom: 14px;
}
.pager {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
}
</style>
