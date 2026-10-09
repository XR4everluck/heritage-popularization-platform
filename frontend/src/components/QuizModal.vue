<script setup>
/**
 * 非遗小测验答题弹窗：5道题逐题作答，答完显示得分和解析
 */
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: Boolean,
  questions: Array,
  answers: Object,
  score: Number
})

const emit = defineEmits(['update:modelValue', 'submit'])

const visible = ref(props.modelValue)
const currentQuestion = ref(0)
const showResult = ref(false)

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    currentQuestion.value = 0
    showResult.value = false
  }
})

watch(() => props.questions, (val) => {
  if (val && val.length > 0) {
    currentQuestion.value = 0
    showResult.value = false
  }
})

function nextQuestion() {
  if (currentQuestion.value < props.questions.length - 1) {
    currentQuestion.value++
  } else {
    showResult.value = true
  }
}

function selectAnswer(option) {
  props.answers[props.questions[currentQuestion.value].id] = option
  nextQuestion()
}

function submitQuiz() {
  emit('submit')
  visible.value = false
}

function close() {
  emit('update:modelValue', false)
}

function resetQuiz() {
  currentQuestion.value = 0
  showResult.value = false
  props.answers = {}
}
</script>

<template>
  <el-dialog v-model="visible" :title="showResult ? '答题结果' : '非遗小测验'" width="600px" destroy-on-close>
    <div v-if="!showResult && questions[currentQuestion]">
      <div class="question-header">
        <div class="question-number">第 {{ currentQuestion + 1 }} 题 / 共 {{ questions.length }} 题</div>
        <div class="question-score">本题 {{ questions[currentQuestion].score }} 分</div>
      </div>
      
      <div class="question-content">
        <div class="question-text">{{ questions[currentQuestion].question }}</div>
        
        <div class="options">
          <el-radio-group v-model="answers[questions[currentQuestion].id]" @change="selectAnswer">
            <el-radio :label="questions[currentQuestion].optionA" class="option-item">
              A. {{ questions[currentQuestion].optionA }}
            </el-radio>
            <el-radio :label="questions[currentQuestion].optionB" class="option-item">
              B. {{ questions[currentQuestion].optionB }}
            </el-radio>
            <el-radio :label="questions[currentQuestion].optionC" class="option-item">
              C. {{ questions[currentQuestion].optionC }}
            </el-radio>
            <el-radio :label="questions[currentQuestion].optionD" class="option-item">
              D. {{ questions[currentQuestion].optionD }}
            </el-radio>
          </el-radio-group>
        </div>
      </div>
      
      <div class="question-footer">
        <el-button type="text" @click="close">取消</el-button>
        <el-button type="primary" @click="nextQuestion" :disabled="!answers[questions[currentQuestion].id]">
          {{ currentQuestion < questions.length - 1 ? '下一题' : '完成答题' }}
        </el-button>
      </div>
    </div>

    <div v-else-if="showResult">
      <div class="result-header">
        <el-icon size="32" color="#c0392b"><Trophy /></el-icon>
        <div class="result-title">答题完成！</div>
        <div class="result-score">得分：{{ score }} / {{ questions.length * 20 }} 分</div>
      </div>
      
      <div class="result-details">
        <div v-for="(question, index) in questions" :key="question.id" class="question-result">
          <div class="question-index">第 {{ index + 1 }} 题</div>
          <div class="question-text">{{ question.question }}</div>
          <div class="answer-info">
            <div class="your-answer">
              <span>你的答案：</span>
              <el-tag :type="answers[question.id] === question.answer ? 'success' : 'danger'">
                {{ answers[question.id] || '未作答' }}
              </el-tag>
            </div>
            <div class="correct-answer">
              <span>正确答案：</span>
              <el-tag type="success">{{ question.answer }}</el-tag>
            </div>
            <div v-if="question.analysis" class="analysis">
              <span>解析：</span>
              <div class="analysis-content">{{ question.analysis }}</div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="result-footer">
        <el-button type="text" @click="resetQuiz">再答一次</el-button>
        <el-button type="primary" @click="submitQuiz">确定</el-button>
      </div>
    </div>

    <template #footer v-if="!showResult">
      <el-button type="text" @click="close">取消</el-button>
      <el-button type="primary" @click="nextQuestion" :disabled="!answers[questions[currentQuestion].id]">
        {{ currentQuestion < questions.length - 1 ? '下一题' : '完成答题' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--gq-border);
}
.question-number {
  color: #8a8578;
  font-size: 14px;
}
.question-score {
  color: var(--gq-primary);
  font-weight: 700;
}
.question-content {
  margin-bottom: 20px;
}
.question-text {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 16px;
  line-height: 1.6;
}
.options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.option-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.question-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}
.result-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--gq-border);
}
.result-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--gq-primary);
  margin: 12px 0;
}
.result-score {
  font-size: 18px;
  color: #c0392b;
  font-weight: 700;
}
.result-details {
  margin-bottom: 24px;
}
.question-result {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px dashed var(--gq-border);
}
.question-index {
  color: #8a8578;
  font-size: 14px;
  margin-bottom: 8px;
}
.question-text {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 12px;
}
.answer-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.your-answer, .correct-answer, .analysis {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}
.your-answer span, .correct-answer span, .analysis span {
  color: #8a8578;
  font-size: 14px;
  white-space: nowrap;
}
.analysis-content {
  color: var(--gq-text);
  font-size: 14px;
  line-height: 1.6;
  margin-top: 4px;
}
.result-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style>