document.addEventListener('DOMContentLoaded', () => {
    // ---------- 1. 动态版权年份 ----------
    const yearSpan = document.getElementById('year');
    if (yearSpan) yearSpan.textContent = new Date().getFullYear();

    // ---------- 2. 汉堡菜单交互 ----------
    const hamburger = document.getElementById('hamburgerBtn');
    const navMenu = document.getElementById('navMenu');
    if (hamburger && navMenu) {
        hamburger.addEventListener('click', () => {
            const isActive = navMenu.classList.toggle('active');
            hamburger.setAttribute('aria-expanded', isActive);
        });
        // 点击菜单项自动关闭，同时增强可访问性
        navMenu.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', () => {
                navMenu.classList.remove('active');
                hamburger.setAttribute('aria-expanded', 'false');
            });
        });
    }

    // ---------- 3. 卡片数据 & 动态生成 ----------
    const cardData = [
        { emoji: '🌿', title: '晨间笔记', desc: '每天五分钟，记录灵感和心情。' },
        { emoji: '📚', title: '深度阅读', desc: '如何从一本书中榨取最大价值。' },
        { emoji: '☕', title: '咖啡哲学', desc: '一杯咖啡的时间，思考生活。' },
        { emoji: '🎧', title: '听的学问', desc: '播客与音乐，碎片时间充电。' },
        { emoji: '✍️', title: '写作习惯', desc: '从一句话开始，建立写作节奏。' },
        { emoji: '🧘', title: '正念时刻', desc: '呼吸之间，找到内心的平静。' }
    ];

    const cardGrid = document.getElementById('cardGrid');
    if (!cardGrid) return;

    // 清空 (保证只有动态内容)
    cardGrid.innerHTML = '';

    // 存储卡片元素的like状态 (用于事件委托)
    const likeStates = new WeakMap();

    cardData.forEach((item, index) => {
        const card = document.createElement('article');
        card.className = 'card';
        card.setAttribute('role', 'article');

        // 图片占位 (用emoji背景色块)
        const imageDiv = document.createElement('div');
        imageDiv.className = 'card-image';
        imageDiv.setAttribute('aria-hidden', 'true');
        imageDiv.textContent = item.emoji;

        // 标题
        const title = document.createElement('h3');
        title.textContent = item.title;

        // 描述
        const desc = document.createElement('p');
        desc.textContent = item.desc;

        // 底部：点赞区
        const footer = document.createElement('div');
        footer.className = 'card-footer';

        const likeBtn = document.createElement('button');
        likeBtn.className = 'like-btn';
        likeBtn.type = 'button';
        likeBtn.setAttribute('aria-label', `点赞 ${item.title}`);

        const likeCount = document.createElement('span');
        likeCount.className = 'like-count';
        likeCount.textContent = '0';

        const heartSpan = document.createElement('span');
        heartSpan.textContent = '♡';   // 空心爱心

        likeBtn.appendChild(heartSpan);
        likeBtn.appendChild(likeCount);
        footer.appendChild(likeBtn);

        // 组装卡片
        card.appendChild(imageDiv);
        card.appendChild(title);
        card.appendChild(desc);
        card.appendChild(footer);

        // 存储初始状态 (使用likeCount作为引用)
        likeStates.set(likeCount, 0);

        cardGrid.appendChild(card);
    });

    // ---------- 4. 点赞事件委托 (性能优化) ----------
    cardGrid.addEventListener('click', (e) => {
        const likeBtn = e.target.closest('.like-btn');
        if (!likeBtn) return;

        // 找到卡片内部的计数span
        const countSpan = likeBtn.querySelector('.like-count');
        if (!countSpan) return;

        // 获取当前点赞数 (从状态存储或解析文本)
        let current = likeStates.get(countSpan);
        if (current === undefined) {
            current = parseInt(countSpan.textContent, 10) || 0;
        }
        current += 1;
        countSpan.textContent = current;
        likeStates.set(countSpan, current);

        // 添加视觉反馈 (切换类)
        likeBtn.classList.add('liked');
        // 可选替换心形
        const heart = likeBtn.querySelector('span:first-child');
        if (heart) heart.textContent = '❤️';
    });

    // ---------- 5. 卡片hover优雅降级：无额外操作 ----------
    // 所有交互已就绪，无占位符。
});