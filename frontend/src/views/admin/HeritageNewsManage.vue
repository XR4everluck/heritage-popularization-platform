<script setup>
/**
 * 科普快讯管理：分页列表（标题/状态筛选）+ 新增/编辑（封面图上传、发布时间）+ 删除 + 置顶 + 发布/草稿切换
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { heritageNewsApi, heritageApi } from '../../api/admin'
import FileUpload from '../../components/FileUpload.vue'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 10, keyword: '', status: null })

const heritageOptions = ref([])

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const emptyForm = {
  id: null,
  title: '',
  content: '',
  heritageId: null,
  coverImage: '',
  isTop: 0,
  publishTime: null,
  sort: 1,
  status: 0
}
const form = reactive({ ...emptyForm })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入正文', trigger: 'blur' }]
}

onMounted(() => {
  loadHeritageOptions()
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await heritageNewsApi.page(query)
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

async function loadHeritageOptions() {
  const res = await heritageApi.page({ page: 1, pageSize: 100 })
  heritageOptions.value = res.data.records || []
}

function openAdd() {
  Object.assign(form, { ...emptyForm, sort: list.value.length + 1 })
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, { ...emptyForm, ...row })
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = { ...form }
    if (payload.heritageId === '' || payload.heritageId === undefined) {
      payload.heritageId = null
    }
    // 发布但未填发布时间时补当前时间，避免"已发布却前台不展示"
    if (payload.status === 1 && !payload.publishTime) {
      payload.publishTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    payload.id ? await heritageNewsApi.update(payload) : await heritageNewsApi.add(payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除快讯【${row.title}】吗？`, '提示', { type: 'warning' })
  await heritageNewsApi.remove(row.id)
  ElMessage.success('已删除')
  loadData()
}

/** 发布/草稿切换 */
async function toggleStatus(row) {
  const target = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(
    `确定将快讯【${row.title}】${target === 1 ? '发布' : '转为草稿'}吗？`,
    '提示', { type: 'warning' }
  )
  await heritageNewsApi.updateStatus(row.id, target)
  row.status = target
  ElMessage.success(target === 1 ? '已发布' : '已转为草稿')
}

/** 置顶开关 */
async function toggleTop(row) {
  const target = row.isTop === 1 ? 0 : 1
  await heritageNewsApi.updateTop(row.id, target)
  row.isTop = target
  ElMessage.success(target === 1 ? '已置顶' : '已取消置顶')
}
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="标题关键词" clearable style="width: 220px"
                @keyup.enter="loadData" @clear="loadData" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 130px" @change="loadData">
        <el-option label="已发布" :value="1" />
        <el-option label="草稿" :value="0" />
      </el-select>
      <el-button type="danger" @click="loadData"><el-icon><Search /></el-icon>&nbsp;查询</el-button>
      <el-button type="primary" plain @click="openAdd"><el-icon><Plus /></el-icon>&nbsp;新增快讯</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="封面" width="120">
        <template #default="{ row }">
          <img v-if="row.coverImage" :src="row.coverImage"
               style="width: 100px; height: 60px; object-fit: cover; border-radius: 6px" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column label="关联非遗" min-width="140" show-overflow-tooltip>
        <template #default="{ row }">{{ row.heritageName || row.heritageId || '-' }}</template>
      </el-table-column>
      <el-table-column label="置顶" width="100" align="center">
        <template #default="{ row }">
          <el-switch :model-value="row.isTop === 1" inline-prompt active-text="顶" inactive-text="否"
                     @change="toggleTop(row)" />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" inline-prompt active-text="发布" inactive-text="草稿"
                     @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="170">
        <template #default="{ row }">{{ row.publishTime || '-' }}</template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" align="center" />
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
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑快讯' : '新增快讯'" width="760px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" maxlength="120" /></el-form-item>
        <el-form-item label="正文" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" maxlength="10000" show-word-limit />
        </el-form-item>
        <el-form-item label="所属非遗">
          <el-select v-model="form.heritageId" placeholder="可留空" clearable filterable style="width: 280px">
            <el-option v-for="item in heritageOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图">
          <div class="cover-box">
            <FileUpload v-model="form.coverImage" type="image" width="240px" height="150px" />
            <el-input v-model="form.coverImage" placeholder="或直接粘贴图片 URL" clearable class="cover-input" />
          </div>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="form.publishTime" type="datetime" placeholder="留空则发布时由前端填入当前时间"
                          value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="排序号"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">发布</el-radio>
            <el-radio :value="0">草稿</el-radio>
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
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
}
.pager {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
}
.cover-box {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.cover-input {
  width: 320px;
}
</style>
