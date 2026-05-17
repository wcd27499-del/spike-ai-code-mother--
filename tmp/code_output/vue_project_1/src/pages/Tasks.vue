<template>
  <div class="tasks-page">
    <div class="task-form">
      <input v-model="newTask" @keyup.enter="addTask" placeholder="输入新任务..." class="input" />
      <button @click="addTask" class="btn">添加</button>
    </div>
    <div class="filters">
      <button v-for="f in filters" :key="f.key" @click="currentFilter = f.key" :class="['filter-btn', { active: currentFilter === f.key }]">{{ f.label }}</button>
    </div>
    <div v-if="filteredTasks.length === 0" class="empty">暂无任务</div>
    <div v-for="task in filteredTasks" :key="task.id" class="task-item" :class="{ done: task.done }">
      <input type="checkbox" v-model="task.done" class="checkbox" />
      <span class="task-text" @dblclick="editTask(task)">{{ task.text }}</span>
      <button @click="removeTask(task.id)" class="del-btn">✕</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const tasks = ref([
  { id: 1, text: '完成项目需求文档', done: false },
  { id: 2, text: '设计数据库表结构', done: false },
  { id: 3, text: '编写核心接口代码', done: true },
  { id: 4, text: '部署测试环境', done: false },
])
const newTask = ref('')
const currentFilter = ref('all')
const nextId = ref(5)

const filters = [
  { key: 'all', label: '全部' },
  { key: 'active', label: '待办' },
  { key: 'done', label: '已完成' },
]

const filteredTasks = computed(() => {
  if (currentFilter.value === 'active') return tasks.value.filter(t => !t.done)
  if (currentFilter.value === 'done') return tasks.value.filter(t => t.done)
  return tasks.value
})

function addTask() {
  const text = newTask.value.trim()
  if (!text) return
  tasks.value.push({ id: nextId.value++, text, done: false })
  newTask.value = ''
}

function removeTask(id) {
  tasks.value = tasks.value.filter(t => t.id !== id)
}

function editTask(task) {
  const text = prompt('编辑任务：', task.text)
  if (text && text.trim()) task.text = text.trim()
}
</script>

<style>
.task-form { display: flex; gap: 8px; margin-bottom: 16px; }
.input { flex: 1; padding: 10px 14px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 14px; outline: none; }
.input:focus { border-color: #1a73e8; }
.btn { padding: 10px 20px; background: #1a73e8; color: #fff; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; }
.btn:hover { background: #1557b0; }
.filters { display: flex; gap: 8px; margin-bottom: 16px; }
.filter-btn { padding: 6px 14px; border: 1px solid #d9d9d9; border-radius: 16px; background: #fff; cursor: pointer; font-size: 13px; color: #555; }
.filter-btn.active { background: #1a73e8; color: #fff; border-color: #1a73e8; }
.task-item { display: flex; align-items: center; gap: 12px; padding: 12px 14px; background: #fff; border-radius: 6px; margin-bottom: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
.task-item.done .task-text { text-decoration: line-through; color: #999; }
.checkbox { width: 18px; height: 18px; cursor: pointer; }
.task-text { flex: 1; font-size: 15px; cursor: pointer; }
.del-btn { width: 28px; height: 28px; border: none; background: #f0f0f0; border-radius: 50%; cursor: pointer; color: #999; font-size: 13px; display: flex; align-items: center; justify-content: center; }
.del-btn:hover { background: #ff4d4f; color: #fff; }
.empty { text-align: center; color: #999; padding: 40px 0; font-size: 15px; }
</style>
