document.addEventListener('DOMContentLoaded',()=>{
    const grid=document.getElementById('cardGrid');
    if(!grid)return;
    const data=[
        {emoji:'🌱',title:'晨间代码',desc:'每天三行新思路。'},
        {emoji:'📦',title:'组件宇宙',desc:'封装、复用、组合。'},
        {emoji:'🧩',title:'设计模式',desc:'在混乱中建立秩序。'},
        {emoji:'⚡',title:'性能碎片',desc:'每一毫秒都珍贵。'},
        {emoji:'🌀',title:'CSS 幻境',desc:'网格与弹性。'},
        {emoji:'🚀',title:'部署手记',desc:'从本地到云端。'}
    ];
    grid.innerHTML='';
    data.forEach((item,i)=>{
        const card=document.createElement('article');
        card.className='card';
        card.innerHTML=`
            <div class="card__img" role="img" aria-label="${item.title}">${item.emoji}</div>
            <h3 class="card__title">${item.title}</h3>
            <p class="card__desc">${item.desc}</p>
            <div class="card__like-area">
                <button class="card__btn" data-like-btn aria-label="点赞 ${item.title}">👍 <span class="card__count" data-count>0</span></button>
            </div>
        `;
        grid.appendChild(card);
    });
    grid.addEventListener('click',e=>{
        const btn=e.target.closest('[data-like-btn]');
        if(!btn)return;
        const span=btn.querySelector('[data-count]');
        if(span)span.textContent=(parseInt(span.textContent,10)||0)+1;
    });
    const toggle=document.querySelector('.nav__toggle');
    const menu=document.querySelector('.nav__menu');
    if(toggle&&menu){
        toggle.addEventListener('click',()=>{
            menu.classList.toggle('nav__menu--open');
            toggle.setAttribute('aria-expanded',menu.classList.contains('nav__menu--open')?'true':'false');
        });
        menu.addEventListener('click',e=>{
            if(e.target.classList.contains('nav__link')&&window.innerWidth<=640){
                menu.classList.remove('nav__menu--open');
                toggle.setAttribute('aria-expanded','false');
            }
        });
    }
    const year=document.getElementById('year');
    if(year)year.textContent=new Date().getFullYear();
});