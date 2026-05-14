document.addEventListener('DOMContentLoaded', () => {
    // 版权年份
    document.getElementById('year').textContent = new Date().getFullYear();

    // 汉堡菜单
    const hamburger = document.getElementById('hamburger');
    const menu = document.getElementById('menu');
    hamburger.addEventListener('click', () => {
        menu.classList.toggle('open');
    });

    // 生成卡片数据
    const posts = [
        { emoji: '🌿', title: '晨间笔记', desc: '一日之计在于晨，安静记录灵感。' },
        { emoji: '📚', title: '阅读随想', desc: '最近在读《设计系统》，收获颇丰。' },
        { emoji: '☕', title: '咖啡时光', desc: '手冲一杯耶加雪菲，酸质明亮。' },
        { emoji: '🎬', title: '电影短评', desc: '《寄生虫》的叙事节奏太厉害了。' },
        { emoji: '🧠', title: '想法碎片', desc: '少即是多，保持核心简洁。' },
        { emoji: '🌱', title: '成长日志', desc: '每天进步一点点，坚持下去。' }
    ];

    const grid = document.getElementById('cardGrid');
    const fragment = document.createDocumentFragment();

    posts.forEach((post, index) => {
        const card = document.createElement('article');
        card.className = 'card';
        // 每张卡片不同渐变色
        const colors = [
            ['#c8e6c9','#a5d6a7'],
            ['#ffe0b2','#ffcc80'],
            ['#b3e5fc','#81d4fa'],
            ['#f8bbd0','#f48fb1'],
            ['#e1bee7','#ce93d8'],
            ['#ffecb3','#ffe082']
        ];
        const [c1, c2] = colors[index % colors.length];
        card.innerHTML = `
            <div class="card-image" style="background: linear-gradient(135deg, ${c1}, ${c2});">${post.emoji}</div>
            <h2>${post.title}</h2>
            <p>${post.desc}</p>
            <div class="card-footer">
                <button class="like-btn" data-likes="0">❤️ <span class="like-count">0</span></button>
                <span>· 刚刚</span>
            </div>
        `;
        fragment.appendChild(card);
    });

    grid.appendChild(fragment);

    // 点赞功能 (事件委托)
    grid.addEventListener('click', (e) => {
        const btn = e.target.closest('.like-btn');
        if (!btn) return;
        const countSpan = btn.querySelector('.like-count');
        let likes = parseInt(btn.dataset.likes, 10);
        likes += 1;
        btn.dataset.likes = likes;
        countSpan.textContent = likes;
    });
});