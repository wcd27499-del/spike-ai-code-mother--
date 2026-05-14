document.addEventListener('DOMContentLoaded', function () {
  // ---------- 动态生成 6 张卡片 ----------
  const cardGrid = document.getElementById('cardGrid');
  if (!cardGrid) return;

  const blogData = [
    { emoji: '🌱', title: '晨间笔记', desc: '每日灵感与碎片思考，用文字唤醒大脑。' },
    { emoji: '📘', title: '代码小记', desc: 'React 状态管理、CSS 网格实用模式。' },
    { emoji: '🎨', title: '设计随想', desc: '极简与留白——减法背后的加法。' },
    { emoji: '📷', title: '街拍日志', desc: '光影、线条、陌生人：城市叙事。' },
    { emoji: '🧠', title: '心智模型', desc: '第一性原理、复利思维在写作中的应用。' },
    { emoji: '📡', title: '工具推荐', desc: '开源神器：终端、笔记、自动化。' }
  ];

  // 存储每张卡片的点赞数（用 data 属性或闭包）
  let likeCounts = new Array(blogData.length).fill(0);

  // 生成卡片 HTML 并一次性插入（避免多次回流）
  const cardsHTML = blogData.map((item, index) => {
    const count = likeCounts[index];
    return `
      <article class="card" data-index="${index}">
        <div class="card-image" role="img" aria-label="${item.title} 图标">${item.emoji}</div>
        <h2 class="card-title">${item.title}</h2>
        <p class="card-desc">${item.desc}</p>
        <div class="card-footer">
          <button class="like-btn" data-index="${index}" aria-label="点赞 ${item.title}">
            <span>👍</span>
            <span class="like-count" id="likeCount-${index}">${count}</span>
          </button>
        </div>
      </article>
    `;
  }).join('');

  cardGrid.innerHTML = cardsHTML;

  // ---------- 点赞事件 (事件委托) ----------
  cardGrid.addEventListener('click', function (e) {
    const btn = e.target.closest('.like-btn');
    if (!btn) return;

    const index = parseInt(btn.getAttribute('data-index'), 10);
    if (isNaN(index) || index < 0 || index >= likeCounts.length) return;

    // 更新计数
    likeCounts[index] += 1;
    const countSpan = document.getElementById(`likeCount-${index}`);
    if (countSpan) {
      countSpan.textContent = likeCounts[index];
    }
  });

  // ---------- 汉堡菜单交互 ----------
  const toggleBtn = document.getElementById('menuToggle');
  const navMenu = document.getElementById('navMenu');

  if (toggleBtn && navMenu) {
    toggleBtn.addEventListener('click', function () {
      navMenu.classList.toggle('active');
      // 可访问性提示（选做）
      const expanded = navMenu.classList.contains('active');
      toggleBtn.setAttribute('aria-expanded', expanded);
    });

    // 点击菜单外部关闭（优雅降级）
    document.addEventListener('click', function (e) {
      if (!toggleBtn.contains(e.target) && !navMenu.contains(e.target)) {
        navMenu.classList.remove('active');
        toggleBtn.setAttribute('aria-expanded', 'false');
      }
    });
  }

  // ---------- 动态更新版权年份 ----------
  const yearSpan = document.getElementById('yearSpan');
  if (yearSpan) {
    yearSpan.textContent = new Date().getFullYear();
  }

  // 初始化汉堡菜单 aria 状态
  if (toggleBtn) {
    toggleBtn.setAttribute('aria-expanded', 'false');
  }
});