<script setup>
/**
 * 个人中心：我的收藏 / 我的评论 / 我的学习 / 个人资料（Tab 切换）
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyCollections, removeCollection, getMyComments, deleteMyComment,
         getMyProgress } from '../../api/front'
import { getProfile, updateProfile } from '../../api/user'
import { useUserStore } from '../../store/user'
import FileUpload from '../../components/FileUpload.vue'
import { defaultCover } from '../../utils/placeholder'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('collection')
// 我的收藏
const collections = ref([])
// 我的评论
const comments = ref([])
const commentTotal = ref(0)
const commentPage = ref(1)
// 我的学习
const progressList = ref([])
// 个人资料
const profileForm = ref({ nickname: '', phone: '', email: '', avatar: '', introduction: '' })
const saving = ref(false)

onMounted(() => {
  loadCollections()
  loadComments()
  loadProgress()
  loadProfile()
})

async function loadCollections() {
  const res = await getMyCollections()
  collections.value = res.data
}

async function loadComments() {
  const res = await getMyComments({ page: commentPage.value, pageSize: 10 })
  comments.value = res.data.records
  commentTotal.value = Number(res.data.total)
}

async function loadProgress() {
  const res = await getMyProgress({})
  progressList.value = res.data
}

async function loadProfile() {
  const res = await getProfile()
  const { nickname, phone, email, avatar, introduction } = res.data || {}
  profileForm.value = { nickname, phone, email, avatar, introduction }
}

async function cancelCollect(row) {
  await ElMessageBox.confirm(`确定取消收藏《${row.name}》吗？`, '提示', { type: 'warning' })
  await removeCollection(row.heritageId)
  ElMessage.success('已取消收藏')
  loadCollections()
}

async function removeComment(row) {
  await ElMessageBox.confirm('确定删除这条评论吗？', '提示', { type: 'warning' })
  await deleteMyComment(row.id)
  ElMessage.success('已删除')
  loadComments()
}

async function saveProfile() {
  saving.value = true
  try {
    await updateProfile(profileForm.value)
    ElMessage.success('资料已更新')
    // 同步刷新顶部登录态中的昵称/头像
    const res = await getProfile()
    userStore.setLogin(userStore.token, res.data)
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <el-card shadow="never" class="profile-card">
    <el-tabs v-model="activeTab">
      <!-- 我的收藏 -->
      <el-tab-pane label="我的收藏" name="collection">
        <el-empty v-if="!collections.length" description="还没有收藏，去非遗博览看看吧" />
        <div v-for="item in collections" :key="item.id" class="collect-item" @click="router.push(`/heritage/${item.heritageId}`)">
          <img :src="item.coverImage || defaultCover(item.name, 200, 140)" class="collect-cover" />
          <div class="collect-info">
            <div class="collect-name">{{ item.name }} <el-tag size="small" type="danger">{{ item.level }}</el-tag></div>
            <div class="collect-meta">{{ item.region }} · 收藏于 {{ item.collectionTime }}</div>
          </div>
          <el-button size="small" plain @click.stop="cancelCollect(item)">取消收藏</el-button>
        </div>
      </el-tab-pane>

      <!-- 我的评论 -->
      <el-tab-pane label="我的评论" name="comment">
        <el-empty v-if="!comments.length" description="暂无评论" />
        <div v-for="item in comments" :key="item.id" class="comment-row">
          <div class="comment-main">
            <div class="comment-text">{{ item.content }}</div>
            <div class="comment-meta">
              评论于《{{ item.heritageName }}》 · {{ item.createTime }}
              <el-tag v-if="item.status === 0" size="small" type="info" style="margin-left: 6px">已被管理员屏蔽</el-tag>
            </div>
          </div>
          <el-button size="small" type="danger" plain @click="removeComment(item)">删除</el-button>
        </div>
        <div class="pager" v-if="commentTotal > 10">
          <el-pagination v-model:current-page="commentPage" :page-size="10" :total="commentTotal"
                         layout="prev, pager, next" background @current-change="loadComments" />
        </div>
      </el-tab-pane>

      <!-- 我的学习 -->
      <el-tab-pane label="我的学习" name="study">
        <el-empty v-if="!progressList.length" description="暂无学习记录，开始你的第一门课程吧" />
        <div v-for="item in progressList" :key="item.id" class="study-item" @click="router.push(`/course/${item.courseId}`)">
          <div class="study-main">
            <div class="study-course">{{ item.courseName }}</div>
            <div class="study-chapter">{{ item.chapterTitle }}</div>
          </div>
          <div class="study-right">
            <el-tag size="small" :type="item.finished ? 'success' : 'info'">{{ item.finished ? '已完成' : '学习中' }}</el-tag>
            <span class="study-duration">{{ item.studyDuration }} 分钟</span>
          </div>
        </div>
      </el-tab-pane>

      <!-- 个人资料 -->
      <el-tab-pane label="个人资料" name="profile">
        <el-form :model="profileForm" label-width="80px" style="max-width: 520px; margin-top: 10px">
          <el-form-item label="头像"><FileUpload v-model="profileForm.avatar" type="image" /></el-form-item>
          <el-form-item label="昵称"><el-input v-model="profileForm.nickname" maxlength="50" /></el-form-item>
          <el-form-item label="手机号"><el-input v-model="profileForm.phone" maxlength="20" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="profileForm.email" maxlength="100" /></el-form-item>
          <el-form-item label="个人简介">
            <el-input v-model="profileForm.introduction" type="textarea" :rows="3" maxlength="500" />
          </el-form-item>
          <el-form-item>
            <el-button type="danger" :loading="saving" @click="saveProfile">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<style scoped>
.profile-card {
  min-height: 480px;
}
.collect-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
}
.collect-item:hover {
  background: #fdf6ec;
}
.collect-cover {
  width: 100px;
  height: 70px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
}
.collect-info {
  flex: 1;
}
.collect-name {
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}
.collect-meta {
  color: #909399;
  font-size: 12px;
  margin-top: 6px;
}
.comment-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 0;
  border-bottom: 1px dashed #ebeef5;
}
.comment-main {
  flex: 1;
}
.comment-text {
  line-height: 1.6;
}
.comment-meta {
  color: #c0c4cc;
  font-size: 12px;
  margin-top: 4px;
}
.study-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 10px;
  border-radius: 8px;
  cursor: pointer;
}
.study-item:hover {
  background: #fdf6ec;
}
.study-course {
  font-weight: 600;
}
.study-chapter {
  color: #909399;
  font-size: 13px;
  margin-top: 4px;
}
.study-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.study-duration {
  color: #909399;
  font-size: 13px;
}
.pager {
  display: flex;
  justify-content: center;
  padding-top: 12px;
}
</style>
