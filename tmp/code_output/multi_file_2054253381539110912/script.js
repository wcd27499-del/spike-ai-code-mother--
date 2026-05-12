document.addEventListener('DOMContentLoaded', () => {
    const grid = document.getElementById('cardGrid');
    const cardsData = [
        { title: 'Post 1', desc: 'First post description.', emoji: '📝' },
        { title: 'Post 2', desc: 'Second post description.', emoji: '💡' },
        { title: 'Post 3', desc: 'Third post description.', emoji: '🚀' },
        { title: 'Post 4', desc: 'Fourth post description.', emoji: '🎉' },
        { title: 'Post 5', desc: 'Fifth post description.', emoji: '🌟' },
        { title: 'Post 6', desc: 'Sixth post description.', emoji: '🔥' }
    ];
    cardsData.forEach((card, index) => {
        const article = document.createElement('article');
        article.className = 'card';
        article.innerHTML = `
            <div class="emoji">${card.emoji}</div>
            <h3>${card.title}</h3>
            <p>${card.desc}</p>
            <button class="like-btn" aria-pressed="false">Like <span class="like-count">0</span></button>
        `;
        const likeBtn = article.querySelector('.like-btn');
        const likeCount = article.querySelector('.like-count');
        likeBtn.addEventListener('click', () => {
            let count = parseInt(likeCount.textContent);
            count++;
            likeCount.textContent = count;
            likeBtn.setAttribute('aria-pressed', 'true');
        });
        grid.appendChild(article);
    });

    // Hamburger menu
    const hamburger = document.getElementById('hamburger');
    const navLinks = document.getElementById('navLinks');
    hamburger.addEventListener('click', () => {
        navLinks.classList.toggle('active');
    });

    // Footer year
    document.getElementById('year').textContent = new Date().getFullYear();
});