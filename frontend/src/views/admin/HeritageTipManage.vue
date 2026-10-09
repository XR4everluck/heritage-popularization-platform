<script setup>
/**
 * 冷知识管理：分页列表（标题筛选）+ 新增/编辑（可关联非遗或作为通用冷知识）+ 删除 + 启用禁用
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { heritageTipApi, heritageApi } from '../../api/admin'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 10, keyword: '', status: null })

const heritageOptions = ref([])

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const emptyForm = { id: null, title: '', content: '', heritageId: null, sort: 1, status: 1 }
const form = reactive({ ...emptyForm })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入冷知识内容', trigger: 'blur' }]
}

onMounted(() => {
  loadHeritageOptions()
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await heritageTipApi.page(query)
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
    // 未选择非遗项目即为通用冷知识，提交 null
    if (payload.heritageId === '' || payload.heritageId === undefined) {
      payload.heritageId = null
    }
    payload.id ? await heritageTipApi.update(payload) : await heritageTipApi.add(payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除冷知识【${row.title}】吗？`, '提示', { type: 'warning' })
  await heritageTipApi.remove(row.id)
  ElMessage.success('已删除')
  loadData()
}

/** 启用/禁用（禁用后前台不推送该冷知识） */
async function toggleStatus(row) {
  const target = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(
    `确定${target === 0 ? '禁用' : '启用'}冷知识【${row.title}】吗？`,
    '提示', { type: 'warning' }
  )
  await heritageTipApi.updateStatus(row.id, target)
  row.status = target
  ElMessage.success(target === 0 ? '已禁用' : '已启用')
}
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="标题关键词" clearable style="width: 220px"
                @keyup.enter="loadData" @clear="loadData" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 130px" @change="loadData">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="danger" @click="loadData"><el-icon><Search /></el-icon>&nbsp;查询</el-button>
      <el-button type="primary" plain @click="openAdd"><el-icon><Plus /></el-icon>&nbsp;新增冷知识</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
      <el-table-column label="所属非遗" min-width="150" show-overflow-tooltip>
        <template #default="{ row }">
          <el-tag v-if="!row.heritageId" size="small" type="warning">通用冷知识</el-tag>
          <span v-else>{{ row.heritageName || row.heritageId }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" align="center" />
      <el-table-column label="状态" width="110" align="center">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" inline-prompt active-text="启用" inactive-text="禁用"
                     @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170">
        <template #default="{ row }">{{ row.createTime || '-' }}</template>
      </el-table-column>
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
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑冷知识' : '新增冷知识'" width="680px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" maxlength="100" /></el-form-item>
        <el-form-item label="冷知识内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item label="所属非遗">
          <el-select v-model="form.heritageId" placeholder="不选则为通用冷知识" clearable filterable style="width: 280px">
            <el-option v-for="item in heritageOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序号"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
</style>
