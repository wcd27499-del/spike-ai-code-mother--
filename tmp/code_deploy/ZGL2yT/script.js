document.addEventListener('DOMContentLoaded', function() {
  // ---------- 动态版权年份 ----------
  document.getElementById('year').textContent = new Date().getFullYear();

  // ---------- 汉堡菜单交互 ----------
  const toggleBtn = document.querySelector('.nav__toggle');
  const menu = document.querySelector('.nav__menu');
  if (toggleBtn && menu) {
    toggleBtn.addEventListener('click', function(e) {
      const expanded = this.getAttribute('aria-expanded') === 'true' ? false : true;
      this.setAttribute('aria-expanded', expanded);
      menu.classList.toggle('open');
      // 改变按钮图标 (可选)
      this.textContent = expanded ? '✕' : '☰';
    });

    // 点击菜单链接后自动关闭 (移动端体验)
    menu.querySelectorAll('.nav__link').forEach(link => {
      link.addEventListener('click', () => {
        if (window.innerWidth <= 640) {
          menu.classList.remove('open');
          toggleBtn.setAttribute('aria-expanded', 'false');
          toggleBtn.textContent = '☰';
        }
      });
    });
  }

  // ---------- 卡片数据 (6张) ----------
  const cardData = [
    { emoji: '🌌', title: '星夜絮语', desc: '仰望深渊，每一粒光都是亿万年的回响。宇宙从不言语，却写满答案。', tag: '天文' },
    { emoji: '📖', title: '书页之间', desc: '翻动纸页，指尖触碰的是另一个灵魂的温度。阅读是隐秘的对话。', tag: '阅读' },
    { emoji: '☕', title: '咖啡哲学', desc: '苦与香在杯中旋转，像极了晨起时分的清醒与恍惚。', tag: '随笔' },
    { emoji: '🎵', title: '旋律容器', desc: '音符是情绪的容器，听一首老歌，忽然就回到了某个秋天。', tag: '音乐' },
    { emoji: '🌱', title: '窗台植物', desc: '绿萝越过书架，把触角伸向阳光。沉默的生长最有力。', tag: '生活' },
    { emoji: '🗺️', title: '未抵达的', desc: '地图上那些陌生地名，像诗一样排列。出发本身就是意义。', tag: '旅行' }
  ];

  const grid = document.getElementById('cardGrid');
  if (!grid) return;

  // 生成卡片 (无占位符)
  cardData.forEach((item, index) => {
    const card = document.createElement('article');
    card.className = 'card';
    card.setAttribute('aria-label', `卡片: ${item.title}`);

    // 图片占位 (背景色 + emoji)
    const imgPlaceholder = document.createElement('div');
    imgPlaceholder.className = 'card__img-placeholder';
    imgPlaceholder.setAttribute('aria-hidden', 'true');
    imgPlaceholder.textContent = item.emoji;

    // 标题
    const title = document.createElement('h2');
    title.className = 'card__title';
    title.textContent = item.title;

    // 描述
    const desc = document.createElement('p');
    desc.className = 'card__desc';
    desc.textContent = item.desc;

    // 底部操作: 标签 + 点赞按钮
    const actions = document.createElement('div');
    actions.className = 'card__actions';

    const tag = document.createElement('span');
    tag.className = 'card__tag';
    tag.textContent = item.tag;

    const likeBtn = document.createElement('button');
    likeBtn.className = 'like-btn';
    likeBtn.type = 'button';
    likeBtn.setAttribute('aria-label', `点赞「${item.title}」`);
    // 初始点赞数 (每张卡片基数可个性化，这里统一 0 但可以有点小随机)
    let count = Math.floor(Math.random() * 5);  // 0~4 让初始不全是0
    likeBtn.innerHTML = `👍 <span class="like-count">${count}</span>`;

    // 点赞事件
    likeBtn.addEventListener('click', function(e) {
      const countSpan = this.querySelector('.like-count');
      let current = parseInt(countSpan.textContent, 10);
      countSpan.textContent = current + 1;
      // 微小动画反馈
      this.style.transform = 'scale(0.92)';
      setTimeout(() => { this.style.transform = ''; }, 120);
    });

    actions.appendChild(tag);
    actions.appendChild(likeBtn);

    card.appendChild(imgPlaceholder);
    card.appendChild(title);
    card.appendChild(desc);
    card.appendChild(actions);

    grid.appendChild(card);
  });

  // 额外小细节: 卡片入场的微透明 (无库实现)
  const cards = document.querySelectorAll('.card');
  cards.forEach((card, i) => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(12px)';
    card.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
    setTimeout(() => {
      card.style.opacity = '1';
      card.style.transform = 'translateY(0)';
    }, 80 + i * 60);
  });
});