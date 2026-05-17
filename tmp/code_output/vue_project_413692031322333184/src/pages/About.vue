<script setup>
</script>

<template>
  <div class="container">
    <div class="about-card">
      <img src="https://picsum.photos/seed/avatar/200/200" alt="头像" class="avatar" />
      <h1>关于我</h1>
      <p class="desc">
        你好，我是一名前端开发者，热爱技术和写作。这个博客是我记录学习心得和生活感悟的小天地。
      </p>
      <p class="desc">
        我相信技术可以改变世界，也相信文字能够温暖人心。希望通过这个博客，与更多志同道合的朋友交流成长。
      </p>
      <div class="info">
        <span>📧 xiaocha@example.com</span>
        <span>📍 中国 · 北京</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.about-card {
  background: #fff;
  border-radius: 12px;
  padding: 50px 40px;
  text-align: center;
  max-width: 600px;
  margin: 20px auto;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 24px;
}

h1 {
  font-size: 26px;
  color: #2c3e50;
  margin-bottom: 16px;
}

.desc {
  color: #666;
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 12px;
  text-align: left;
}

.info {
  margin-top: 28px;
  display: flex;
  justify-content: center;
  gap: 30px;
  color: #888;
  font-size: 14px;
}

@media (max-width: 600px) {
  .about-card {
    padding: 30px 20px;
  }
  .info {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
