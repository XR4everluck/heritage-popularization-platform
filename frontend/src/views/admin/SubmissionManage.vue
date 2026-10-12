<script setup>
/**
 * 内容审核管理（docs/17）：审核用户发布的科普快讯 / 科普视频。
 * 通过 → 前台首页展示；驳回 → 用户端显示驳回原因。
 */
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { submissionApi } from '../../api/admin'

const loading = ref(false)
const list = ref([])
// 状态筛选：'' 全部 / 0 待审核 / 1 已通过 / 2 未通过
const statusFilter = ref('')

const STATUS_META = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '已通过', type: 'success' },
  2: { text: '未通过', type: 'danger' }
}

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await submissionApi.list({ status: statusFilter.value })
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function onFilterChange() {
  loadData()
}

/** 通过审核 */
async function approve(row) {
  await ElMessageBox.confirm(`确定通过《${row.title}》并发布到前台吗？`, '审核通过', { type: 'warning' })
  await submissionApi.review(row.id, { status: 1, reviewNote: '审核通过' })
  ElMessage.success('已通过并发布')
  loadData()
}

/** 驳回（需填写理由） */
async function reject(row) {
  const { value } = await ElMessageBox.prompt(`驳回《${row.title}》，请填写驳回原因（将展示给提交者）：`, '审核驳回', {
    type: 'warning',
    inputPlaceholder: '例如：内容与非遗主题无关',
    inputValidator: (v) => (v && v.trim() ? true : '驳回原因不能为空')
  })
  await submissionApi.review(row.id, { status: 2, reviewNote: value.trim() })
  ElMessage.success('已驳回')
  loadData()
}

/** 下架已通过内容（全民科普/首页随之隐藏） */
async function offline(row) {
  const { value } = await ElMessageBox.prompt(`下架《${row.title}》，请填写下架原因（可简述）：`, '内容下架', {
    type: 'warning',
    inputPlaceholder: '例如：图片版权待确认'
  })
  await submissionApi.offline(row.id, value ? value.trim() : '')
  ElMessage.success('已下架')
  loadData()
}

/** 内容摘要（去换行截断） */
function excerpt(text, length = 60) {
  const t = (text || '').replace(/\s+/g, ' ').trim()
  return t.length > length ? `${t.slice(0, length)}…` : t
}
</script>

<template>
  <div class="submission-manage">
    <div class="toolbar">
      <el-radio-group v-model="statusFilter" @change="onFilterChange">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button :value="0">待审核</el-radio-button>
        <el-radio-button :value="1">已通过</el-radio-button>
        <el-radio-button :value="2">未通过</el-radio-button>
      </el-radio-group>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="row.type === 'course' ? 'success' : 'info'" effect="plain">
            {{ row.type === 'course' ? '科普视频' : '科普快讯' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column label="内容" min-width="220">
        <template #default="{ row }">
          <span class="cell-excerpt">{{ excerpt(row.content) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="heritageName" label="关联项目" width="150" show-overflow-tooltip>
        <template #default="{ row }">{{ row.heritageName || '—' }}</template>
      </el-table-column>
      <el-table-column prop="submitter" label="提交人" width="110" />
      <el-table-column prop="submitTime" label="提交时间" width="160" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag size="small" :type="STATUS_META[row.status]?.type">{{ STATUS_META[row.status]?.text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button size="small" type="danger" @click="approve(row)">通过</el-button>
            <el-button size="small" @click="reject(row)">驳回</el-button>
          </template>
          <template v-else-if="row.status === 1">
            <el-button size="small" plain @click="offline(row)">下架</el-button>
            <el-tooltip :content="`审核意见：${row.reviewNote || '—'}（${row.reviewTime || ''}）`" placement="top">
              <span class="reviewed-note">已审核</span>
            </el-tooltip>
          </template>
          <el-tooltip v-else :content="`驳回原因：${row.reviewNote || '—'}（${row.reviewTime || ''}）`" placement="top">
            <span class="reviewed-note">已驳回</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无内容" :image-size="60" />
      </template>
    </el-table>
  </div>
</template>

<style scoped>
.submission-manage {
  padding: 16px;
}
.toolbar {
  margin-bottom: 14px;
}
.cell-excerpt {
  color: var(--gq-text-secondary);
  font-size: 13px;
}
.reviewed-note {
  color: var(--gq-text-muted);
  font-size: 13px;
  cursor: help;
  border-bottom: 1px dashed var(--gq-border);
}
</style>
