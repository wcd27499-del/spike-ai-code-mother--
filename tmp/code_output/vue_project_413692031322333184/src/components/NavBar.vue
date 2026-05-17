<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
const links = [
  { path: '/', label: '首页' },
  { path: '/about', label: '关于' }
]
</script>

<template>
  <header class="navbar">
    <div class="container navbar-inner">
      <router-link to="/" class="logo">午后茶歇</router-link>
      <nav class="nav-links">
        <router-link
          v-for="link in links"
          :key="link.path"
          :to="link.path"
          :class="{ active: route.path === link.path }"
        >
          {{ link.label }}
        </router-link>
      </nav>
    </div>
  </header>
</template>

<style scoped>
.navbar {
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 10;
}

.navbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
  letter-spacing: 1px;
}

.nav-links {
  display: flex;
  gap: 24px;
}

.nav-links a {
  font-size: 15px;
  color: #666;
  padding: 4px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
}

.nav-links a:hover,
.nav-links a.active {
  color: #2c3e50;
  border-bottom-color: #2c3e50;
}

@media (max-width: 480px) {
  .navbar-inner {
    height: 50px;
  }
  .logo {
    font-size: 17px;
  }
  .nav-links a {
    font-size: 14px;
  }
}
</style>
