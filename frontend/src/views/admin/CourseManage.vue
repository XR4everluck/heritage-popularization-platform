<script setup>
/**
 * 课程管理：课程分页 + 增删改查 + 章节管理（抽屉：章节列表、视频上传、排序）
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { courseApi, heritageApi } from '../../api/admin'
import FileUpload from '../../components/FileUpload.vue'
import { defaultCover } from '../../utils/placeholder'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 10, heritageId: null, keyword: '' })
const heritages = ref([])

// 课程对话框
const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const form = reactive({ id: null, heritageId: null, name: '', summary: '', cover: '', teacher: '', duration: 60, publishTime: null })
const rules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  heritageId: [{ required: true, message: '请选择关联非遗项目', trigger: 'change' }]
}

// 章节管理抽屉
const drawerVisible = ref(false)
const currentCourse = ref(null)
const chapters = ref([])
const chapterDialogVisible = ref(false)
const chapterSaving = ref(false)
const chapterFormRef = ref()
const chapterForm = reactive({ id: null, title: '', videoUrl: '', content: '', sort: 1 })
const chapterRules = {
  title: [{ required: true, message: '请输入章节标题', trigger: 'blur' }]
}

onMounted(async () => {
  const res = await heritageApi.page({ page: 1, pageSize: 100 })
  heritages.value = res.data.records
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await courseApi.page(query)
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

function heritageName(id) {
  return heritages.value.find((h) => h.id === id)?.name || '-'
}

function openAdd() {
  Object.assign(form, { id: null, heritageId: null, name: '', summary: '', cover: '', teacher: '', duration: 60, publishTime: null })
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
    form.id ? await courseApi.update({ ...form }) : await courseApi.add({ ...form })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除课程【${row.name}】吗？`, '提示', { type: 'warning' })
  await courseApi.remove(row.id)
  ElMessage.success('已删除')
  loadData()
}

/** 打开章节管理抽屉 */
async function openChapters(row) {
  currentCourse.value = row
  drawerVisible.value = true
  const res = await courseApi.chapterList(row.id)
  chapters.value = res.data
}

function openChapterAdd() {
  Object.assign(chapterForm, { id: null, title: '', videoUrl: '', content: '', sort: chapters.value.length + 1 })
  chapterDialogVisible.value = true
}

function openChapterEdit(row) {
  Object.assign(chapterForm, row)
  chapterDialogVisible.value = true
}

async function saveChapter() {
  await chapterFormRef.value.validate()
  chapterSaving.value = true
  try {
    if (chapterForm.id) {
      await courseApi.chapterUpdate({ ...chapterForm })
    } else {
      await courseApi.chapterAdd({ ...chapterForm, courseId: currentCourse.value.id })
    }
    ElMessage.success('保存成功')
    chapterDialogVisible.value = false
    openChapters(currentCourse.value)
  } finally {
    chapterSaving.value = false
  }
}

async function removeChapter(row) {
  await ElMessageBox.confirm(`确定删除章节【${row.title}】吗？`, '提示', { type: 'warning' })
  await courseApi.chapterRemove(row.id)
  ElMessage.success('已删除')
  openChapters(currentCourse.value)
}
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-select v-model="query.heritageId" placeholder="全部非遗项目" clearable filterable style="width: 200px" @change="loadData">
        <el-option v-for="h in heritages" :key="h.id" :label="h.name" :value="h.id" />
      </el-select>
      <el-input v-model="query.keyword" placeholder="课程名称关键词" clearable style="width: 220px"
                @keyup.enter="loadData" @clear="loadData" />
      <el-button type="danger" @click="loadData"><el-icon><Search /></el-icon>&nbsp;查询</el-button>
      <el-button type="primary" plain @click="openAdd"><el-icon><Plus /></el-icon>&nbsp;新增课程</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column label="封面" width="90">
        <template #default="{ row }">
          <img :src="row.cover || defaultCover(row.name, 120, 80)" style="width: 60px; height: 40px; object-fit: cover; border-radius: 4px" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="课程名称" min-width="180" show-overflow-tooltip />
      <el-table-column label="关联非遗" min-width="130">
        <template #default="{ row }">{{ heritageName(row.heritageId) }}</template>
      </el-table-column>
      <el-table-column prop="teacher" label="讲师" width="130" show-overflow-tooltip />
      <el-table-column prop="duration" label="总时长(分)" width="100" align="center" />
      <el-table-column prop="viewCount" label="浏览" width="80" align="center" />
      <el-table-column prop="publishTime" label="发布时间" width="170">
        <template #default="{ row }">{{ row.publishTime || '未发布' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="success" plain @click="openChapters(row)">章节</el-button>
          <el-button size="small" type="primary" plain @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" plain @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination v-model:current-page="query.page" :page-size="query.pageSize" :total="total"
                     layout="total, prev, pager, next" background @current-change="loadData" />
    </div>

    <!-- 课程对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑课程' : '新增课程'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="name"><el-input v-model="form.name" maxlength="100" /></el-form-item>
        <el-form-item label="关联非遗" prop="heritageId">
          <el-select v-model="form.heritageId" filterable placeholder="选择非遗项目">
            <el-option v-for="h in heritages" :key="h.id" :label="h.name" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="讲师"><el-input v-model="form.teacher" maxlength="50" /></el-form-item>
        <el-form-item label="总时长(分)"><el-input-number v-model="form.duration" :min="0" /></el-form-item>
        <el-form-item label="课程封面"><FileUpload v-model="form.cover" type="image" /></el-form-item>
        <el-form-item label="课程简介"><el-input v-model="form.summary" type="textarea" :rows="3" maxlength="500" /></el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="form.publishTime" type="datetime" placeholder="留空表示未发布"
                          value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <!-- 章节管理抽屉 -->
    <el-drawer v-model="drawerVisible" :title="`章节管理 - ${currentCourse?.name || ''}`" size="46%">
      <el-button type="primary" plain style="margin-bottom: 12px" @click="openChapterAdd">
        <el-icon><Plus /></el-icon>&nbsp;新增章节
      </el-button>
      <el-table :data="chapters" stripe>
        <el-table-column prop="sort" label="序号" width="60" align="center" />
        <el-table-column prop="title" label="章节标题" min-width="180" show-overflow-tooltip />
        <el-table-column label="视频" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.videoUrl ? 'success' : 'info'">{{ row.videoUrl ? '已上传' : '未上传' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="openChapterEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="removeChapter(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 章节对话框 -->
      <el-dialog v-model="chapterDialogVisible" :title="chapterForm.id ? '编辑章节' : '新增章节'" width="520px" append-to-body>
        <el-form ref="chapterFormRef" :model="chapterForm" :rules="chapterRules" label-width="90px">
          <el-form-item label="章节标题" prop="title"><el-input v-model="chapterForm.title" maxlength="100" /></el-form-item>
          <el-form-item label="排序号"><el-input-number v-model="chapterForm.sort" :min="0" /></el-form-item>
          <el-form-item label="章节视频"><FileUpload v-model="chapterForm.videoUrl" type="video" width="280px" height="150px" /></el-form-item>
          <el-form-item label="图文讲义"><el-input v-model="chapterForm.content" type="textarea" :rows="4" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="chapterDialogVisible = false">取消</el-button>
          <el-button type="danger" :loading="chapterSaving" @click="saveChapter">保存</el-button>
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
</style>
