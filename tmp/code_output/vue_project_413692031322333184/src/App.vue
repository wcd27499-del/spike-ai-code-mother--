<script setup>
import NavBar from '@/components/NavBar.vue'
</script>

<template>
  <div class="app">
    <NavBar />
    <main class="main">
      <router-view />
    </main>
    <footer class="footer">
      <div class="container">
        <p>&copy; 2024 午后茶歇 · 用文字记录生活</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main {
  flex: 1;
  padding: 30px 0;
}

.footer {
  background: #fff;
  border-top: 1px solid #eee;
  padding: 20px 0;
  text-align: center;
  color: #999;
  font-size: 14px;
}
</style>
