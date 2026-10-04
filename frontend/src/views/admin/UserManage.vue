<script setup>
/**
 * 用户管理：分页列表（关键词/状态筛选）+ 启用/禁用切换
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '../../api/admin'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 10, keyword: '', status: null })

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await userApi.page(query)
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

/** 启用/禁用账号（禁用后该用户立即无法登录，已签发 token 也会失效） */
async function toggleStatus(row) {
  const target = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(
    `确定${target === 0 ? '禁用' : '启用'}用户【${row.nickname || row.username}】吗？`,
    '提示', { type: 'warning' }
  )
  await userApi.updateStatus(row.id, target)
  row.status = target
  ElMessage.success(target === 0 ? '已禁用' : '已启用')
}
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="用户名/昵称关键词" clearable style="width: 220px"
                @keyup.enter="loadData" @clear="loadData" />
      <el-select v-model="query.status" placeholder="账号状态" clearable style="width: 140px" @change="loadData">
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="danger" @click="loadData"><el-icon><Search /></el-icon>&nbsp;查询</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="头像" width="70">
        <template #default="{ row }">
          <el-avatar :size="34" :src="row.avatar">{{ (row.nickname || row.username).charAt(0) }}</el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" min-width="120" />
      <el-table-column prop="nickname" label="昵称" min-width="120" />
      <el-table-column prop="phone" label="手机号" width="130">
        <template #default="{ row }">{{ row.phone || '-' }}</template>
      </el-table-column>
      <el-table-column label="角色" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="row.role === 'admin' ? 'danger' : 'info'">
            {{ row.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="170" />
      <el-table-column label="状态" width="120" align="center">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" inline-prompt active-text="正常" inactive-text="禁用"
                     :disabled="row.role === 'admin'" @change="toggleStatus(row)" />
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
.pager {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
}
</style>
