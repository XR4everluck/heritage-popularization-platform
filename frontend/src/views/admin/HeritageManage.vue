<script setup>
/**
 * 非遗项目管理：分页表格（分类/级别筛选）+ 新增/编辑（封面图片上传 + 富文本详情）+ 删除
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onBeforeUnmount } from 'vue'
import { heritageApi, categoryApi, historyApi } from '../../api/admin'
import FileUpload from '../../components/FileUpload.vue'
import { defaultCover } from '../../utils/placeholder'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 10, categoryId: null, level: null, keyword: '' })
const categories = ref([])

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const form = reactive({
  id: null, categoryId: null, name: '', level: '国家级', region: '', inheritor: '',
  summary: '', content: '', coverImage: '', publishTime: null,
  originAge: '', distributionArea: '', representativeWorks: '', endangerLevel: '状况良好'
})

const rules = {
  name: [{ required: true, message: '请输入非遗名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  level: [{ required: true, message: '请选择级别', trigger: 'change' }]
}

// ---------- 历史节点管理 ----------
const historyDrawer = ref(false)
const historyProject = ref(null)
const historyList = ref([])
const historyDialog = ref(false)
const historySaving = ref(false)
const historyForm = reactive({ id: null, year: '', event: '', description: '' })

async function openHistory(row) {
  historyProject.value = row
  historyDrawer.value = true
  await loadHistory()
}

async function loadHistory() {
  const res = await historyApi.listByHeritage(historyProject.value.id)
  historyList.value = res.data
}

function openHistoryAdd() {
  Object.assign(historyForm, { id: null, year: '', event: '', description: '' })
  historyDialog.value = true
}

function openHistoryEdit(row) {
  Object.assign(historyForm, row)
  historyDialog.value = true
}

async function saveHistory() {
  if (!historyForm.event?.trim()) return ElMessage.warning('请输入事件标题')
  historySaving.value = true
  try {
    const payload = { ...historyForm, heritageId: historyProject.value.id }
    historyForm.id ? await historyApi.update(payload) : await historyApi.add(payload)
    ElMessage.success('保存成功')
    historyDialog.value = false
    loadHistory()
  } finally {
    historySaving.value = false
  }
}

async function removeHistory(row) {
  await ElMessageBox.confirm(`确定删除历史节点【${row.year} ${row.event}】吗？`, '提示', { type: 'warning' })
  await historyApi.remove(row.id)
  ElMessage.success('已删除')
  loadHistory()
}

// 富文本编辑器实例（对话框关闭时销毁，防止内存泄漏）
const editorRef = ref()
const toolbarConfig = {}
const editorConfig = { placeholder: '请输入非遗详细介绍（支持图文排版）...' }
onBeforeUnmount(() => {
  editorRef.value?.destroy()
})

onMounted(async () => {
  const res = await categoryApi.page({ page: 1, pageSize: 100 })
  categories.value = res.data.records
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await heritageApi.page(query)
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

function openAdd() {
  Object.assign(form, {
    id: null, categoryId: null, name: '', level: '国家级', region: '', inheritor: '',
    summary: '', content: '', coverImage: '', publishTime: null,
    originAge: '', distributionArea: '', representativeWorks: '', endangerLevel: '状况良好'
  })
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
    const payload = { ...form }
    form.id ? await heritageApi.update(payload) : await heritageApi.add(payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除非遗项目【${row.name}】吗？`, '提示', { type: 'warning' })
  await heritageApi.remove(row.id)
  ElMessage.success('已删除')
  loadData()
}
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width: 150px" @change="loadData">
        <el-option v-for="cate in categories" :key="cate.id" :label="cate.name" :value="cate.id" />
      </el-select>
      <el-select v-model="query.level" placeholder="非遗级别" clearable style="width: 130px" @change="loadData">
        <el-option label="国家级" value="国家级" />
        <el-option label="省级" value="省级" />
        <el-option label="市级" value="市级" />
      </el-select>
      <el-input v-model="query.keyword" placeholder="非遗名称关键词" clearable style="width: 220px"
                @keyup.enter="loadData" @clear="loadData" />
      <el-button type="danger" @click="loadData"><el-icon><Search /></el-icon>&nbsp;查询</el-button>
      <el-button type="primary" plain @click="openAdd"><el-icon><Plus /></el-icon>&nbsp;新增项目</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column label="封面" width="90">
        <template #default="{ row }">
          <img :src="row.coverImage || defaultCover(row.name, 120, 80)" style="width: 60px; height: 40px; object-fit: cover; border-radius: 4px" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="130" show-overflow-tooltip />
      <el-table-column label="级别" width="90">
        <template #default="{ row }">
          <el-tag size="small" :type="row.level === '国家级' ? 'danger' : 'warning'">{{ row.level }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="categoryName" label="分类" width="110" />
      <el-table-column prop="region" label="地区" min-width="120" show-overflow-tooltip />
      <el-table-column label="濒危程度" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.endangerLevel" size="small"
                  :type="row.endangerLevel === '濒危' ? 'danger' : row.endangerLevel === '急需保护' ? 'danger' : row.endangerLevel === '脆弱' ? 'warning' : 'success'"
                  effect="plain">{{ row.endangerLevel }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="80" align="center" />
      <el-table-column prop="collectionCount" label="收藏" width="80" align="center" />
      <el-table-column prop="publishTime" label="发布时间" width="170">
        <template #default="{ row }">{{ row.publishTime || '未发布' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" plain @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="warning" plain @click="openHistory(row)">历史</el-button>
          <el-button size="small" type="danger" plain @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination v-model:current-page="query.page" :page-size="query.pageSize" :total="total"
                     layout="total, prev, pager, next" background @current-change="loadData" />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑非遗项目' : '新增非遗项目'" width="760px" top="4vh" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="名称" prop="name"><el-input v-model="form.name" maxlength="100" /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="选择分类">
                <el-option v-for="cate in categories" :key="cate.id" :label="cate.name" :value="cate.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="级别" prop="level">
              <el-select v-model="form.level">
                <el-option label="国家级" value="国家级" />
                <el-option label="省级" value="省级" />
                <el-option label="市级" value="市级" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属地区"><el-input v-model="form.region" maxlength="100" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="传承人"><el-input v-model="form.inheritor" maxlength="50" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="起源年代"><el-input v-model="form.originAge" maxlength="50" placeholder="如：唐代 / 1906年" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="濒危程度">
              <el-select v-model="form.endangerLevel">
                <el-option label="濒危" value="濒危" />
                <el-option label="急需保护" value="急需保护" />
                <el-option label="脆弱" value="脆弱" />
                <el-option label="状况良好" value="状况良好" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="分布地区"><el-input v-model="form.distributionArea" maxlength="255" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="代表作品">
          <el-input v-model="form.representativeWorks" maxlength="500"
                    placeholder="多个作品用、分隔，如：《牡丹亭》《长生殿》" />
        </el-form-item>
        <el-form-item label="封面图片"><FileUpload v-model="form.coverImage" type="image" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.summary" type="textarea" :rows="2" maxlength="500" /></el-form-item>
        <el-form-item label="详细介绍">
          <div class="editor-wrap">
            <Toolbar class="editor-toolbar" :editor="editorRef" :defaultConfig="toolbarConfig" mode="default" />
            <Editor class="editor-body" v-model="form.content" :defaultConfig="editorConfig" mode="default"
                    @onCreated="(editor) => (editorRef.value = editor)" />
          </div>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="form.publishTime" type="datetime" placeholder="留空表示未发布（草稿）"
                          value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <!-- 历史节点管理抽屉 -->
    <el-drawer v-model="historyDrawer" :title="`历史节点 · ${historyProject?.name || ''}`" size="560px">
      <div style="margin-bottom: 12px">
        <el-button type="primary" plain size="small" @click="openHistoryAdd">
          <el-icon><Plus /></el-icon>&nbsp;新增节点
        </el-button>
      </div>
      <el-timeline v-if="historyList.length">
        <el-timeline-item v-for="node in historyList" :key="node.id" :timestamp="node.year" placement="top">
          <el-card shadow="never" :body-style="{ padding: '10px 14px' }">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 8px">
              <div style="flex: 1; min-width: 0">
                <b>{{ node.event }}</b>
                <div style="color: #909399; font-size: 13px; margin-top: 4px">{{ node.description }}</div>
              </div>
              <div style="flex-shrink: 0">
                <el-button size="small" text type="primary" @click="openHistoryEdit(node)">编辑</el-button>
                <el-button size="small" text type="danger" @click="removeHistory(node)">删除</el-button>
              </div>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无历史节点" :image-size="70" />

      <!-- 节点编辑对话框 -->
      <el-dialog v-model="historyDialog" :title="historyForm.id ? '编辑历史节点' : '新增历史节点'" width="460px" append-to-body>
        <el-form label-width="80px">
          <el-form-item label="年代" required><el-input v-model="historyForm.year" maxlength="30" placeholder="如：唐代 / 1955年" /></el-form-item>
          <el-form-item label="事件" required><el-input v-model="historyForm.event" maxlength="200" /></el-form-item>
          <el-form-item label="描述"><el-input v-model="historyForm.description" type="textarea" :rows="3" maxlength="500" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="historyDialog = false">取消</el-button>
          <el-button type="danger" :loading="historySaving" @click="saveHistory">保存</el-button>
        </template>
      </el-dialog>
    </el-drawer>
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
.editor-wrap {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  z-index: 100;
}
.editor-toolbar {
  border-bottom: 1px solid #dcdfe6;
}
.editor-body {
  height: 260px;
  overflow-y: hidden;
}
</style>
