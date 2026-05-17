import { createApp, ref, computed } from 'vue'
import { createRouter, createWebHashHistory } from 'vue-router'

const Tasks = {
  template: `
    <div class="page">
      <h2 class="title">📋 我的任务</h2>
      <div class="form">
        <input v-model="text" @keyup.enter="add" placeholder="输入新任务..." class="inp" />
        <button @click="add" class="btn">添加</button>
      </div>
      <div class="tabs">
        <button v-for="t in tabs" :key="t.k" @click="cur=t.k" :class="['tab',{on:cur===t.k}]">{{ t.l }}</button>
      </div>
      <div v-if="list.length===0" class="empty">暂无任务</div>
      <div v-for="t in list" :key="t.id" :class="['item',{done:t.d}]">
        <input type="checkbox" v-model="t.d" class="cb" />
        <span class="txt" @dblclick="edit(t)">{{ t.c }}</span>
        <button @click="del(t.id)" class="x">✕</button>
      </div>
    </div>
  `,
  setup() {
    const tasks = ref([
      { id:1, c:'完成项目需求文档', d:false },
      { id:2, c:'设计数据库表结构', d:false },
      { id:3, c:'编写核心接口代码', d:true },
      { id:4, c:'部署测试环境', d:false },
    ])
    const text = ref('')
    const cur = ref('all')
    const nid = ref(5)
    const tabs = [{k:'all',l:'全部'},{k:'act',l:'待办'},{k:'done',l:'已完成'}]
    const list = computed(() => {
      if (cur.value==='act') return tasks.value.filter(t => !t.d)
      if (cur.value==='done') return tasks.value.filter(t => t.d)
      return tasks.value
    })
    function add() {
      const v = text.value.trim()
      if (!v) return
      tasks.value.push({ id:nid.value++, c:v, d:false })
      text.value = ''
    }
    function del(id) { tasks.value = tasks.value.filter(t => t.id !== id) }
    function edit(t) {
      const v = prompt('编辑：', t.c)
      if (v && v.trim()) t.c = v.trim()
    }
    return { text, cur, tabs, list, add, del, edit }
  }
}

const router = createRouter({
  history: createWebHashHistory(),
  routes: [{ path: '/', component: Tasks }]
})

createApp({
  template: `
    <div class="app">
      <nav><h1>📋 任务记录</h1><router-link to="/">任务列表</router-link></nav>
      <main><router-view /></main>
    </div>
  `
}).use(router).mount('#app')
