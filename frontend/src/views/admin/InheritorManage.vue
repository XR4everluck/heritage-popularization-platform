<script setup>
/**
 * 传承人管理：分页列表（姓名/状态筛选）+ 新增/编辑（头像上传）+ 删除 + 启用禁用
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { inheritorApi } from '../../api/admin'
import FileUpload from '../../components/FileUpload.vue'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 10, keyword: '', status: null })

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const emptyForm = {
  id: null,
  name: '',
  title: '',
  avatar: '',
  introduction: '',
  experience: '',
  achievements: '',
  tags: '', // 逗号分隔的标签字符串
  heritageId: null,
  status: 1,
  sort: 1
}
const form = reactive({ ...emptyForm })
const rules = {
  name: [{ required: true, message: '请输入传承人姓名', trigger: 'blur' }]
}

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await inheritorApi.page(query)
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
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
    // 未关联非遗时提交 null，避免后端把空字符串当关联 ID
    if (payload.heritageId === '' || payload.heritageId === undefined) {
      payload.heritageId = null
    }
    payload.id ? await inheritorApi.update(payload) : await inheritorApi.add(payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除传承人【${row.name}】吗？`, '提示', { type: 'warning' })
  await inheritorApi.remove(row.id)
  ElMessage.success('已删除')
  loadData()
}

/** 启用/禁用（禁用后前台不展示该传承人） */
async function toggleStatus(row) {
  const target = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(
    `确定${target === 0 ? '禁用' : '启用'}传承人【${row.name}】吗？`,
    '提示', { type: 'warning' }
  )
  await inheritorApi.updateStatus(row.id, target)
  row.status = target
  ElMessage.success(target === 0 ? '已禁用' : '已启用')
}
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="传承人姓名关键词" clearable style="width: 220px"
                @keyup.enter="loadData" @clear="loadData" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 130px" @change="loadData">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="danger" @click="loadData"><el-icon><Search /></el-icon>&nbsp;查询</el-button>
      <el-button type="primary" plain @click="openAdd"><el-icon><Plus /></el-icon>&nbsp;新增传承人</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="头像" width="80">
        <template #default="{ row }">
          <el-avatar :size="40" :src="row.avatar">{{ (row.name || '').charAt(0) }}</el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="姓名" min-width="110" show-overflow-tooltip />
      <el-table-column prop="title" label="头衔" min-width="140" show-overflow-tooltip>
        <template #default="{ row }">{{ row.title || '-' }}</template>
      </el-table-column>
      <el-table-column label="关联非遗" min-width="140" show-overflow-tooltip>
        <template #default="{ row }">{{ row.heritageName || row.heritageId || '-' }}</template>
      </el-table-column>
      <el-table-column prop="tags" label="标签" min-width="140" show-overflow-tooltip>
        <template #default="{ row }">
          <el-tag v-for="(tag, idx) in (row.tags ? row.tags.split(',') : [])" :key="idx" size="small" type="info" class="tag-item">
            {{ tag.trim() }}
          </el-tag>
          <span v-if="!row.tags">-</span>
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
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑传承人' : '新增传承人'" width="720px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="姓名" prop="name"><el-input v-model="form.name" maxlength="50" /></el-form-item>
        <el-form-item label="头衔"><el-input v-model="form.title" placeholder="如 国家级非遗代表性传承人" maxlength="100" /></el-form-item>
        <el-form-item label="头像">
          <FileUpload v-model="form.avatar" type="image" width="180px" height="180px" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="多个标签用英文逗号分隔，如 技艺,剪纸" maxlength="255" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.introduction" type="textarea" :rows="4" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-form-item label="传承经历">
          <el-input v-model="form.experience" type="textarea" :rows="4" maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item label="荣誉成就">
          <el-input v-model="form.achievements" type="textarea" :rows="4" maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item label="关联非遗ID">
          <el-input-number v-model="form.heritageId" :min="1" :controls="false" placeholder="可留空" style="width: 160px" />
          <span class="form-tip">填写非遗项目对应的 ID</span>
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
.tag-item {
  margin-right: 4px;
}
.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}
</style>
