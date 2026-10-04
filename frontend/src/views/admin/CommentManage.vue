<script setup>
/**
 * 评论管理：分页列表（含评论人/非遗信息）+ 屏蔽/恢复 + 删除
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { commentApi } from '../../api/admin'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 10, status: null })

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await commentApi.page(query)
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

/** 屏蔽/恢复评论（屏蔽后前台不展示） */
async function toggleStatus(row) {
  const target = row.status === 1 ? 0 : 1
  await commentApi.updateStatus(row.id, target)
  row.status = target
  ElMessage.success(target === 0 ? '已屏蔽' : '已恢复')
}

async function remove(row) {
  await ElMessageBox.confirm('确定删除这条评论吗？', '提示', { type: 'warning' })
  await commentApi.remove(row.id)
  ElMessage.success('已删除')
  loadData()
}
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-select v-model="query.status" placeholder="评论状态" clearable style="width: 140px" @change="loadData">
        <el-option label="正常" :value="1" />
        <el-option label="已屏蔽" :value="0" />
      </el-select>
      <el-button type="danger" @click="loadData"><el-icon><Search /></el-icon>&nbsp;查询</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="评论人" width="140">
        <template #default="{ row }">
          <div class="user-cell">
            <el-avatar :size="28" :src="row.avatar">{{ row.nickname?.charAt(0) }}</el-avatar>
            {{ row.nickname }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="heritageName" label="非遗项目" min-width="120" show-overflow-tooltip />
      <el-table-column prop="content" label="评论内容" min-width="240" show-overflow-tooltip />
      <el-table-column prop="likeCount" label="点赞" width="70" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '已屏蔽' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="评论时间" width="170" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" plain @click="toggleStatus(row)">
            {{ row.status === 1 ? '屏蔽' : '恢复' }}
          </el-button>
          <el-button size="small" type="danger" plain @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination v-model:current-page="query.page" :page-size="query.pageSize" :total="total"
                     layout="total, prev, pager, next" background @current-change="loadData" />
    </div>
  </el-card>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
}
.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.pager {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
}
</style>
