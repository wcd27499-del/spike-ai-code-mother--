document.addEventListener('DOMContentLoaded', () => {
  // 动态版权年份
  document.getElementById('year').textContent = new Date().getFullYear();

  // 汉堡菜单切换 (无障碍 + aria)
  const toggleBtn = document.querySelector('.nav-toggle');
  const navMenu = document.querySelector('.nav-menu');
  if (toggleBtn && navMenu) {
    toggleBtn.addEventListener('click', () => {
      const expanded = toggleBtn.getAttribute('aria-expanded') === 'true' ? false : true;
      toggleBtn.setAttribute('aria-expanded', expanded);
      navMenu.classList.toggle('active');
      toggleBtn.textContent = expanded ? '✕' : '☰';
    });
    // 点击菜单项自动关闭 (移动端体验)
    navMenu.querySelectorAll('.nav-link').forEach(link => {
      link.addEventListener('click', () => {
        if (window.innerWidth <= 640) {
          navMenu.classList.remove('active');
          toggleBtn.setAttribute('aria-expanded', 'false');
          toggleBtn.textContent = '☰';
        }
      });
    });
  }

  // 卡片数据 (6张, 用emoji/背景色作为占位)
  const cardData = [
    { emoji: '🌊', title: '碧波', desc: '静谧的蓝色水域，倒映天空的流云。' },
    { emoji: '🏔️', title: '山岚', desc: '晨雾缠绕山脊，松林深处有鹿鸣。' },
    { emoji: '🎋', title: '竹隐', desc: '风过翠竹，石阶青苔，幽径通茶寮。' },
    { emoji: '🌿', title: '林间', desc: '蕨类与野花铺成柔软的地毯。' },
    { emoji: '🍂', title: '秋拾', desc: '银杏叶落满长椅，光影斑驳。' },
    { emoji: '🌌', title: '星垂', desc: '暗夜中的银河，像碎钻洒在天鹅绒上。' }
  ];

  const grid = document.querySelector('.card-grid');
  if (!grid) return;

  // 清空并渲染卡片 (无占位, 直接生成)
  grid.innerHTML = '';
  cardData.forEach((item, index) => {
    const card = document.createElement('article');
    card.className = 'card';
    card.setAttribute('aria-label', `卡片: ${item.title}`);

    // 图片占位 (纯色+emoji)
    const imgDiv = document.createElement('div');
    imgDiv.className = 'card-img';
    imgDiv.setAttribute('aria-hidden', 'true');
    // 随机柔和背景色 (基于索引)
    const colors = ['#e3d9fc','#fce4d6','#d7f0e6','#fde8e8','#d6e8f7','#f9e4d4'];
    imgDiv.style.background = `linear-gradient(145deg, ${colors[index % colors.length]}, ${colors[(index+3) % colors.length]})`;
    imgDiv.textContent = item.emoji;

    const body = document.createElement('div');
    body.className = 'card-body';

    const title = document.createElement('h2');
    title.className = 'card-title';
    title.textContent = item.title;

    const desc = document.createElement('p');
    desc.className = 'card-desc';
    desc.textContent = item.desc;

    const footer = document.createElement('div');
    footer.className = 'card-footer';

    const likeBtn = document.createElement('button');
    likeBtn.className = 'like-btn';
    likeBtn.type = 'button';
    likeBtn.setAttribute('aria-label', `点赞 ${item.title}`);
    // 初始点赞数 0
    let likes = 0;
    const countSpan = document.createElement('span');
    countSpan.className = 'like-count';
    countSpan.textContent = likes;
    likeBtn.innerHTML = `<span class="like-icon">👍</span> `;
    likeBtn.appendChild(countSpan);

    // 点击点赞 +1
    likeBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      likes += 1;
      countSpan.textContent = likes;
      // 添加微反馈: 按钮短暂放大
      likeBtn.style.transform = 'scale(1.12)';
      setTimeout(() => { likeBtn.style.transform = ''; }, 180);
    });

    footer.appendChild(likeBtn);
    body.append(title, desc, footer);
    card.append(imgDiv, body);
    grid.appendChild(card);
  });

  // 如果网格中没有卡片（意外情况），显示一条友好消息（不会出现占位）
  if (grid.children.length === 0) {
    const emptyMsg = document.createElement('p');
    emptyMsg.textContent = '暂无卡片，稍后再来看看吧。';
    emptyMsg.style.textAlign = 'center';
    emptyMsg.style.padding = '3rem';
    emptyMsg.style.color = '#6c6c8a';
    grid.appendChild(emptyMsg);
  }

  console.log('✅ 卡片网格已渲染，交互已绑定');
});