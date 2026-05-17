<script setup>
import { posts } from '@/utils/posts'
import PostCard from '@/components/PostCard.vue'
</script>

<template>
  <div class="container">
    <div class="hero">
      <h1>午后茶歇</h1>
      <p>记录技术、生活与思考，在文字里寻找平静。</p>
    </div>
    <div class="post-list">
      <PostCard v-for="post in posts" :key="post.id" :post="post" />
    </div>
  </div>
</template>

<style scoped>
.hero {
  text-align: center;
  padding: 50px 0 40px;
}

.hero h1 {
  font-size: 32px;
  color: #2c3e50;
  margin-bottom: 12px;
}

.hero p {
  color: #888;
  font-size: 16px;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-bottom: 40px;
}

@media (max-width: 600px) {
  .hero {
    padding: 30px 0 25px;
  }
  .hero h1 {
    font-size: 26px;
  }
}
</style>
