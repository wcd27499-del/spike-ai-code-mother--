import { createRouter, createWebHashHistory } from 'vue-router'
import Tasks from '@/pages/Tasks.vue'

const routes = [{ path: '/', name: 'Tasks', component: Tasks }]

export default createRouter({ history: createWebHashHistory(), routes })
