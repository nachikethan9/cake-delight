// Cake Delight - minimal frontend.
// Served by the API Gateway, so every call uses a relative path (/api/...) on the same origin: no CORS needed.

const CATEGORIES = ['BIRTHDAY', 'WEDDING', 'ANNIVERSARY', 'PASTRY', 'CUPCAKE', 'CUSTOM'];
const money = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' });
const $ = (id) => document.getElementById(id);

const state = { page: 0, size: 6 };

// ---------------------------------------------------------------- helpers

/** Escape text before putting it into HTML (prevents XSS from data such as cake names or comments). */
function esc(value) {
    return String(value ?? '').replace(/[&<>"']/g, (c) =>
        ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c]);
}

const userId = () => $('userId').value.trim();
const email = () => $('email').value.trim();
const userPath = () => encodeURIComponent(userId());
const formatDate = (iso) => (iso ? new Date(iso).toLocaleString() : '');

class ApiError extends Error {
    constructor(status, body) {
        super(describeError(status, body));
        this.status = status;
        this.body = body;
    }
}

/** Turn a ProblemDetail response from our services into a readable message. */
function describeError(status, body) {
    if (!body) return `Request failed (${status})`;
    let message = body.detail || body.title || `Request failed (${status})`;
    if (body.errors) message += ': ' + Object.values(body.errors).join(', ');
    return message;
}

/** Small fetch wrapper: sends/receives JSON and throws ApiError for 4xx/5xx. */
async function api(path, { method = 'GET', body } = {}) {
    const response = await fetch(path, {
        method,
        headers: body ? { 'Content-Type': 'application/json' } : {},
        body: body ? JSON.stringify(body) : undefined,
    });
    if (response.status === 204) return null;
    const data = await response.json().catch(() => null);
    if (!response.ok) throw new ApiError(response.status, data);
    return data;
}

let toastTimer;
function toast(message, type = 'ok') {
    const el = $('toast');
    el.textContent = message;
    el.className = `toast ${type}`;
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => (el.className = 'toast hidden'), 4000);
}

/** Run an API action, show success/error toast, return the result (or undefined on error). */
async function run(action, successMessage) {
    try {
        const result = await action();
        if (successMessage) toast(successMessage);
        return result ?? true;
    } catch (error) {
        toast(error.message, 'error');
        return undefined;
    }
}

// ---------------------------------------------------------------- catalog (Catalog + Rating services)

function buildCakeQuery() {
    const params = new URLSearchParams({ page: state.page, size: state.size, sort: $('fSort').value });
    const name = $('fName').value.trim();
    if (name) params.set('name', name);
    if ($('fCategory').value) params.set('category', $('fCategory').value);
    if ($('fMin').value) params.set('minPrice', $('fMin').value);
    if ($('fMax').value) params.set('maxPrice', $('fMax').value);
    if ($('fAvailable').checked) params.set('available', 'true');
    return params;
}

async function loadCakes() {
    const page = await run(() => api(`/api/cakes?${buildCakeQuery()}`));
    if (!page || page === true) return;

    $('pageInfo').textContent =
        `Page ${page.page + 1} of ${Math.max(page.totalPages, 1)} · ${page.totalElements} cakes`;
    $('prevPage').disabled = page.first;
    $('nextPage').disabled = page.last;

    // One summary call per cake (fine for a small page; a real app would offer a batch endpoint)
    const summaries = await Promise.all(
        page.content.map((cake) => api(`/api/ratings/cake/${cake.id}/summary`).catch(() => null)));

    $('cakes').innerHTML = page.content.length
        ? page.content.map((cake, i) => cakeCard(cake, summaries[i])).join('')
        : '<p class="muted">No cakes match these filters.</p>';
}

function ratingText(summary) {
    if (!summary || summary.ratingCount === 0) return '<span class="muted">No ratings yet</span>';
    return `★ ${summary.averageRating.toFixed(1)} <span class="muted">(${summary.ratingCount})</span>`;
}

function cakeCard(cake, summary) {
    const buy = cake.available
        ? `<div class="row">
               <input type="number" min="1" max="20" value="1" id="qty-${cake.id}" class="qty">
               <button data-action="add" data-id="${cake.id}">Add to basket</button>
           </div>`
        : '<p class="sold-out">Currently unavailable</p>';

    const scoreOptions = [5, 4, 3, 2, 1]
        .map((s) => `<option value="${s}">${'★'.repeat(s)}</option>`).join('');

    return `
        <article class="card ${cake.available ? '' : 'unavailable'}">
            <div class="card-head">
                <h3>${esc(cake.name)}</h3>
                <span class="badge">${esc(cake.category)}</span>
            </div>
            <p class="desc">${esc(cake.description)}</p>
            <div class="price-row">
                <strong>${money.format(cake.price)}</strong>
                <span class="rating">${ratingText(summary)}</span>
            </div>
            ${buy}
            <details class="rate">
                <summary>Rate this cake</summary>
                <div class="row">
                    <select id="score-${cake.id}">${scoreOptions}</select>
                    <input id="comment-${cake.id}" placeholder="Comment (optional)" maxlength="500">
                    <button class="secondary" data-action="rate" data-id="${cake.id}">Submit</button>
                </div>
            </details>
        </article>`;
}

async function addToBasket(cakeId) {
    const quantity = Number($(`qty-${cakeId}`).value);
    const basket = await run(
        () => api(`/api/baskets/${userPath()}/items`, { method: 'POST', body: { cakeId, quantity } }),
        'Added to basket');
    if (basket && basket !== true) renderBasket(basket);
}

async function rateCake(cakeId) {
    const body = {
        cakeId,
        userId: userId(),
        score: Number($(`score-${cakeId}`).value),
        comment: $(`comment-${cakeId}`).value.trim() || null,
    };
    const ok = await run(() => api('/api/ratings', { method: 'POST', body }), 'Thanks for your rating!');
    if (ok) loadCakes();
}

// ---------------------------------------------------------------- basket + checkout (Order service)

async function loadBasket() {
    const basket = await run(() => api(`/api/baskets/${userPath()}`));
    if (basket && basket !== true) renderBasket(basket);
}

function renderBasket(basket) {
    const el = $('basket');
    if (!basket.items.length) {
        el.innerHTML = '<p class="muted">Your basket is empty.</p>';
        return;
    }
    el.innerHTML = `
        <ul class="lines">
            ${basket.items.map((item) => `
                <li>
                    <div><strong>${esc(item.cakeName)}</strong><br>
                         <span class="muted">${money.format(item.unitPrice)} each</span></div>
                    <input type="number" min="1" max="20" value="${item.quantity}" class="qty" data-cake="${item.cakeId}">
                    <span class="line-total">${money.format(item.lineTotal)}</span>
                    <button class="icon" data-remove="${item.cakeId}" title="Remove">✕</button>
                </li>`).join('')}
        </ul>
        <div class="total">
            <span>${basket.totalItems} item(s)</span>
            <strong>${money.format(basket.totalAmount)}</strong>
        </div>
        <div class="row">
            <button id="clearBasket" class="secondary">Clear</button>
            <button id="checkout">Checkout</button>
        </div>`;
}

async function updateQuantity(cakeId, quantity) {
    const basket = await run(() => api(`/api/baskets/${userPath()}/items/${cakeId}`,
        { method: 'PUT', body: { quantity } }), 'Quantity updated');
    if (basket && basket !== true) renderBasket(basket); else loadBasket();
}

async function removeItem(cakeId) {
    const basket = await run(() => api(`/api/baskets/${userPath()}/items/${cakeId}`,
        { method: 'DELETE' }), 'Item removed');
    if (basket && basket !== true) renderBasket(basket);
}

async function clearBasket() {
    const ok = await run(() => api(`/api/baskets/${userPath()}`, { method: 'DELETE' }), 'Basket cleared');
    if (ok) loadBasket();
}

async function checkout() {
    if (!email()) return toast('Please enter an email address', 'error');
    const order = await run(() => api('/api/orders/checkout',
        { method: 'POST', body: { userId: userId(), customerEmail: email() } }));
    if (!order || order === true) return;

    toast(`Order #${order.orderId} placed – ${money.format(order.totalAmount)}`);
    await loadBasket();
    await loadOrders();
    // The confirmation travels Order -> Kafka -> Notification, so give it a moment
    setTimeout(loadNotifications, 1500);
}

// ---------------------------------------------------------------- orders + notifications

async function loadOrders() {
    const page = await run(() => api(`/api/orders?userId=${userPath()}&size=5`));
    if (!page || page === true) return;
    $('orders').innerHTML = page.content.length
        ? `<ul class="list">${page.content.map((o) => `
            <li>
                <span>#${o.orderId}<br><span class="muted">${formatDate(o.createdAt)}</span></span>
                <span class="badge status-${esc(o.status)}">${esc(o.status)}</span>
                <strong>${money.format(o.totalAmount)}</strong>
            </li>`).join('')}</ul>`
        : '<p class="muted">No orders yet.</p>';
}

async function loadNotifications() {
    const page = await run(() => api(`/api/notifications?userId=${userPath()}&size=5`));
    if (!page || page === true) return;
    $('notifications').innerHTML = page.content.length
        ? `<ul class="list">${page.content.map((n) => `
            <li>
                <details>
                    <summary><strong>${esc(n.subject)}</strong><br>
                        <span class="muted">to ${esc(n.recipient)} · ${formatDate(n.createdAt)}</span></summary>
                    <pre>${esc(n.message)}</pre>
                    ${n.failureReason ? `<p class="sold-out">${esc(n.failureReason)}</p>` : ''}
                </details>
                <span class="badge status-${esc(n.status)}">${esc(n.status)}</span>
            </li>`).join('')}</ul>`
        : '<p class="muted">No notifications yet.</p>';
}

function loadUserData() {
    if (!userId()) return toast('Please enter a user ID', 'error');
    loadBasket();
    loadOrders();
    loadNotifications();
}

// ---------------------------------------------------------------- wiring

$('fCategory').innerHTML = '<option value="">All categories</option>' +
    CATEGORIES.map((c) => `<option value="${c}">${c}</option>`).join('');

$('filters').addEventListener('submit', (e) => { e.preventDefault(); state.page = 0; loadCakes(); });
$('resetFilters').addEventListener('click', () => { $('filters').reset(); state.page = 0; loadCakes(); });
$('prevPage').addEventListener('click', () => { state.page--; loadCakes(); });
$('nextPage').addEventListener('click', () => { state.page++; loadCakes(); });

$('switchUser').addEventListener('click', loadUserData);
$('refreshOrders').addEventListener('click', loadOrders);
$('refreshNotifications').addEventListener('click', loadNotifications);

$('cakes').addEventListener('click', (e) => {
    const button = e.target.closest('button[data-action]');
    if (!button) return;
    const cakeId = Number(button.dataset.id);
    if (button.dataset.action === 'add') addToBasket(cakeId);
    if (button.dataset.action === 'rate') rateCake(cakeId);
});

$('basket').addEventListener('change', (e) => {
    if (e.target.matches('input[data-cake]')) {
        updateQuantity(Number(e.target.dataset.cake), Number(e.target.value));
    }
});

$('basket').addEventListener('click', (e) => {
    if (e.target.matches('[data-remove]')) removeItem(Number(e.target.dataset.remove));
    if (e.target.id === 'clearBasket') clearBasket();
    if (e.target.id === 'checkout') checkout();
});

loadCakes();
loadUserData();
