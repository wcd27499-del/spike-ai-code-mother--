<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPostById } from '@/utils/posts'

const route = useRoute()
const router = useRouter()

const post = computed(() => getPostById(route.params.id))
</script>

<template>
  <div class="container">
    <div v-if="post" class="article">
      <img :src="post.cover" :alt="post.title" class="cover" />
      <div class="meta">
        <h1>{{ post.title }}</h1>
        <span class="date">{{ post.date }}</span>
      </div>
      <div class="content" v-html="post.content.replace(/\n/g, '<br>')"></div>
      <button class="back-btn" @click="router.push('/')">← 返回首页</button>
    </div>
    <div v-else class="not-found">
      <h2>文章不存在</h2>
      <button class="back-btn" @click="router.push('/')">← 返回首页</button>
    </div>
  </div>
</template>

<style scoped>
.article {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  padding-bottom: 30px;
}

.cover {
  width: 100%;
  height: 320px;
  object-fit: cover;
  display: block;
}

.meta {
  padding: 30px 30px 0;
}

.meta h1 {
  font-size: 26px;
  color: #2c3e50;
  margin-bottom: 10px;
}

.date {
  color: #999;
  font-size: 14px;
}

.content {
  padding: 24px 30px 20px;
  font-size: 16px;
  color: #444;
  line-height: 1.9;
}

.content :deep(h2) {
  font-size: 20px;
  color: #2c3e50;
  margin: 28px 0 12px;
}

.content :deep(p) {
  margin-bottom: 14px;
}

.not-found {
  text-align: center;
  padding: 80px 0;
  color: #999;
}

.back-btn {
  display: block;
  margin: 0 30px;
  padding: 10px 24px;
  background: #f0f0f0;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  color: #555;
  cursor: pointer;
  transition: background 0.2s;
}

.back-btn:hover {
  background: #e0e0e0;
}

@media (max-width: 600px) {
  .cover {
    height: 200px;
  }
  .meta {
    padding: 20px 20px 0;
  }
  .meta h1 {
    font-size: 22px;
  }
  .content {
    padding: 16px 20px;
  }
  .back-btn {
    margin: 0 20px;
  }
}
</style>
