<template>
  <div class="app">
    <nav class="nav">
      <h1 class="logo">📋 任务记录</h1>
      <router-link to="/" class="nav-link">我的任务</router-link>
    </nav>
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f0f2f5; color: #333; }
.app { max-width: 800px; margin: 0 auto; padding: 0 16px; }
.nav { display: flex; align-items: center; gap: 24px; padding: 16px 0; border-bottom: 1px solid #e8e8e8; }
.logo { font-size: 20px; color: #1a73e8; }
.nav-link { text-decoration: none; color: #555; font-size: 15px; }
.nav-link:hover { color: #1a73e8; }
.main { padding: 24px 0; }
@media (max-width: 600px) { .nav { flex-direction: column; align-items: flex-start; gap: 8px; } }
</style>
