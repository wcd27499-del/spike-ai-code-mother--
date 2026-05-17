<script setup>
defineProps({
  post: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <router-link :to="`/article/${post.id}`" class="card">
    <div class="card-img">
      <img :src="post.cover" :alt="post.title" />
    </div>
    <div class="card-body">
      <span class="date">{{ post.date }}</span>
      <h2>{{ post.title }}</h2>
      <p>{{ post.summary }}</p>
      <span class="read-more">阅读更多 →</span>
    </div>
  </router-link>
</template>

<style scoped>
.card {
  display: flex;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}

.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.08);
}

.card-img {
  width: 280px;
  min-height: 180px;
  flex-shrink: 0;
}

.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.card-body {
  padding: 24px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.date {
  font-size: 13px;
  color: #aaa;
  margin-bottom: 8px;
}

.card-body h2 {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 10px;
  line-height: 1.4;
}

.card-body p {
  color: #777;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.read-more {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

@media (max-width: 680px) {
  .card {
    flex-direction: column;
  }
  .card-img {
    width: 100%;
    height: 200px;
  }
  .card-body {
    padding: 18px 20px;
  }
  .card-body h2 {
    font-size: 18px;
  }
}
</style>
