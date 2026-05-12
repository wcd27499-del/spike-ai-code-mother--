document.addEventListener('DOMContentLoaded', () => {
    // 更新版权年份
    const yearSpan = document.getElementById('year');
    if (yearSpan) yearSpan.textContent = new Date().getFullYear();

    // 汉堡菜单交互
    const hamburger = document.getElementById('hamburger');
    const navMenu = document.getElementById('nav-menu');
    if (hamburger && navMenu) {
        hamburger.addEventListener('click', () => {
            const expanded = hamburger.getAttribute('aria-expanded') === 'true' ? false : true;
            hamburger.setAttribute('aria-expanded', expanded);
            navMenu.classList.toggle('active');
        });
        // 点击菜单项关闭（移动端）
        navMenu.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', () => {
                navMenu.classList.remove('active');
                hamburger.setAttribute('aria-expanded', 'false');
            });
        });
    }

    // 动态生成6张卡片
    const grid = document.getElementById('card-grid');
    if (!grid) return;

    const cardData = [
        { title: '晨曦', desc: '清晨的第一缕光，温暖而宁静。', color: '#fde68a', emoji: '🌅' },
        { title: '森林', desc: '翠绿深处，呼吸自然的气息。', color: '#bbf7d0', emoji: '🌲' },
        { title: '海洋', desc: '碧波万顷，聆听潮汐的低语。', color: '#bae6fd', emoji: '🌊' },
        { title: '山峰', desc: '登高望远，俯瞰云海壮阔。', color: '#e9d5ff', emoji: '⛰️' },
        { title: '繁星', desc: '夜空里闪烁的，是远古的光。', color: '#fecaca', emoji: '✨' },
        { title: '花园', desc: '花开荼蘼，蝴蝶与蜜蜂共舞。', color: '#d9f99d', emoji: '🌸' }
    ];

    // 存储点赞状态 (用 Map 或对象，防止全局污染)
    const likesMap = new Map();

    cardData.forEach((item, index) => {
        const card = document.createElement('article');
        card.className = 'card';
        card.setAttribute('aria-label', `卡片: ${item.title}`);

        const iconDiv = document.createElement('div');
        iconDiv.className = 'card-icon';
        iconDiv.style.background = item.color;
        iconDiv.textContent = item.emoji;

        const title = document.createElement('h3');
        title.textContent = item.title;

        const desc = document.createElement('p');
        desc.textContent = item.desc;

        const likeBtn = document.createElement('button');
        likeBtn.className = 'like-btn';
        likeBtn.type = 'button';
        likeBtn.setAttribute('aria-label', `点赞 ${item.title}`);
        // 初始点赞数 0
        likesMap.set(index, 0);
        likeBtn.innerHTML = `👍 <span id="like-count-${index}">0</span>`;

        // 点赞逻辑
        likeBtn.addEventListener('click', () => {
            let current = likesMap.get(index) || 0;
            current += 1;
            likesMap.set(index, current);
            const countSpan = document.getElementById(`like-count-${index}`);
            if (countSpan) countSpan.textContent = current;
            // 增加微反馈
            likeBtn.style.transform = 'scale(0.95)';
            setTimeout(() => { likeBtn.style.transform = ''; }, 150);
        });

        card.append(iconDiv, title, desc, likeBtn);
        grid.appendChild(card);
    });
});