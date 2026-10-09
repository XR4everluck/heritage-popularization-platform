<script setup>
/**
 * 题库管理：按非遗项目筛选 + 分页列表 + 新增/编辑 + 删除 + 启用禁用 + JSON 批量导入
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { quizQuestionApi, heritageApi } from '../../api/admin'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 10, heritageId: null, keyword: '', status: null })

// 非遗项目下拉数据
const heritageOptions = ref([])

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const emptyForm = {
  id: null,
  question: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  correctAnswer: 'A',
  analysis: '',
  score: 5,
  difficulty: 1,
  heritageId: null,
  questionType: 1,
  status: 1,
  sort: 1
}
const form = reactive({ ...emptyForm })
const rules = {
  question: [{ required: true, message: '请输入题干', trigger: 'blur' }],
  optionA: [{ required: true, message: '请输入选项 A', trigger: 'blur' }],
  optionB: [{ required: true, message: '请输入选项 B', trigger: 'blur' }],
  correctAnswer: [{ required: true, message: '请选择正确答案', trigger: 'change' }]
}

// 批量导入
const importVisible = ref(false)
const importText = ref('')
const importing = ref(false)

onMounted(() => {
  loadHeritageOptions()
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await quizQuestionApi.page(query)
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
  Object.assign(form, { ...emptyForm, sort: list.value.length + 1, heritageId: query.heritageId || null })
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
    payload.id ? await quizQuestionApi.update(payload) : await quizQuestionApi.add(payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除题目【${row.question}】吗？`, '提示', { type: 'warning' })
  await quizQuestionApi.remove(row.id)
  ElMessage.success('已删除')
  loadData()
}

/** 启用/禁用（禁用后不参与随机抽题） */
async function toggleStatus(row) {
  const target = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(
    `确定${target === 0 ? '禁用' : '启用'}该题目吗？`,
    '提示', { type: 'warning' }
  )
  await quizQuestionApi.updateStatus(row.id, target)
  row.status = target
  ElMessage.success(target === 0 ? '已禁用' : '已启用')
}

function openImport() {
  importText.value = ''
  importVisible.value = true
}

/** 解析 textarea 中的 JSON 数组并批量导入 */
async function doImport() {
  let arr
  try {
    arr = JSON.parse(importText.value)
  } catch (e) {
    ElMessage.error('JSON 解析失败，请检查格式是否为合法的 JSON 数组')
    return
  }
  if (!Array.isArray(arr) || arr.length === 0) {
    ElMessage.error('请粘贴非空的 JSON 数组')
    return
  }
  importing.value = true
  try {
    const res = await quizQuestionApi.batchImport(arr)
    // 后端可能返回新增条数或新增后的列表，两种都兼容
    const count = typeof res.data === 'number' ? res.data : (Array.isArray(res.data) ? res.data.length : arr.length)
    ElMessage.success(`成功导入 ${count} 条题目`)
    importVisible.value = false
    loadData()
  } finally {
    importing.value = false
  }
}
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-select v-model="query.heritageId" placeholder="按非遗项目筛选" clearable filterable style="width: 220px"
                 @change="loadData">
        <el-option v-for="item in heritageOptions" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-input v-model="query.keyword" placeholder="题干关键词" clearable style="width: 220px"
                @keyup.enter="loadData" @clear="loadData" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 130px" @change="loadData">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="danger" @click="loadData"><el-icon><Search /></el-icon>&nbsp;查询</el-button>
      <el-button type="primary" plain @click="openAdd"><el-icon><Plus /></el-icon>&nbsp;新增题目</el-button>
      <el-button type="warning" plain @click="openImport"><el-icon><Upload /></el-icon>&nbsp;批量导入</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="question" label="题干" min-width="240" show-overflow-tooltip />
      <el-table-column label="正确答案" width="100" align="center">
        <template #default="{ row }">
          <el-tag size="small" type="success">{{ row.correctAnswer }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="score" label="分值" width="80" align="center" />
      <el-table-column label="难度" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.difficulty === 1 ? 'success' : row.difficulty === 2 ? 'warning' : 'danger'">
            {{ row.difficulty === 1 ? '简单' : row.difficulty === 2 ? '中等' : '困难' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="题型" width="90" align="center">
        <template #default="{ row }">
          {{ row.questionType === 1 ? '单选' : row.questionType === 2 ? '多选' : '判断' }}
        </template>
      </el-table-column>
      <el-table-column label="关联非遗" min-width="140" show-overflow-tooltip>
        <template #default="{ row }">{{ row.heritageName || row.heritageId || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" inline-prompt active-text="启用" inactive-text="禁用"
                     @change="toggleStatus(row)" />
        </template>
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
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑题目' : '新增题目'" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="题干" prop="question">
          <el-input v-model="form.question" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="选项 A" prop="optionA"><el-input v-model="form.optionA" maxlength="200" /></el-form-item>
        <el-form-item label="选项 B" prop="optionB"><el-input v-model="form.optionB" maxlength="200" /></el-form-item>
        <el-form-item label="选项 C"><el-input v-model="form.optionC" maxlength="200" /></el-form-item>
        <el-form-item label="选项 D"><el-input v-model="form.optionD" maxlength="200" /></el-form-item>
        <el-form-item label="正确答案" prop="correctAnswer">
          <el-radio-group v-model="form.correctAnswer">
            <el-radio value="A">A</el-radio>
            <el-radio value="B">B</el-radio>
            <el-radio value="C">C</el-radio>
            <el-radio value="D">D</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" :rows="3" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-form-item label="分值"><el-input-number v-model="form.score" :min="1" /></el-form-item>
        <el-form-item label="难度">
          <el-select v-model="form.difficulty" style="width: 160px">
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="form.questionType" style="width: 160px">
            <el-option label="单选题" :value="1" />
            <el-option label="多选题" :value="2" />
            <el-option label="判断题" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属非遗">
          <el-select v-model="form.heritageId" placeholder="可留空" clearable filterable style="width: 260px">
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

    <!-- 批量导入对话框 -->
    <el-dialog v-model="importVisible" title="批量导入题目" width="640px">
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
                title="粘贴 JSON 数组，字段：question / optionA / optionB / optionC / optionD / correctAnswer / analysis / score / difficulty / heritageId" />
      <el-input v-model="importText" type="textarea" :rows="12" placeholder='[{"question":"...","optionA":"...","optionB":"...","optionC":"...","optionD":"...","correctAnswer":"A","score":5,"difficulty":1,"heritageId":1}]' />
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="warning" :loading="importing" @click="doImport">导入</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}
.pager {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
}
</style>
