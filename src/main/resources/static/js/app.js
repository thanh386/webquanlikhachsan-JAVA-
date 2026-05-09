document.addEventListener('DOMContentLoaded', () => {
    // --- Scroll Reveal Logic ---
    const revealElements = document.querySelectorAll('[data-reveal]');
    
    const header = document.getElementById('main-header');
    
    const revealOnScroll = () => {
        // Header Scroll Effect
        if (header) {
            if (window.scrollY > 50) {
                header.classList.add('is-scrolled');
            } else {
                header.classList.remove('is-scrolled');
            }
        }

        // Element Reveal Effect
        revealElements.forEach(el => {
            const elementTop = el.getBoundingClientRect().top;
            const windowHeight = window.innerHeight;
            if (elementTop < windowHeight - 50) {
                el.classList.add('is-visible');
            }
        });
    };

    window.addEventListener('scroll', revealOnScroll);
    revealOnScroll(); // Trigger once on load

    // --- Toast Notifications ---
    const toastRoot = document.getElementById('toast-root');
    const toastMessages = document.querySelectorAll('[data-toast-message]');

    const createToast = (message, type = 'success') => {
        if (!toastRoot) return;
        
        const toast = document.createElement('div');
        toast.className = `flex items-center gap-3 bg-white p-4 rounded-2xl shadow-heavy border-l-4 pointer-events-auto transition-all duration-500 transform translate-x-full opacity-0 ${
            type === 'success' ? 'border-emerald-500' : 'border-red-500'
        }`;
        
        toast.innerHTML = `
            <div class="grid h-8 w-8 place-items-center rounded-full ${type === 'success' ? 'bg-emerald-50 text-emerald-600' : 'bg-red-50 text-red-600'}">
                <i class="fa-solid ${type === 'success' ? 'fa-check' : 'fa-exclamation'}"></i>
            </div>
            <p class="text-sm font-bold text-slate-700">${message}</p>
        `;
        
        toastRoot.appendChild(toast);
        
        // Animate in
        requestAnimationFrame(() => {
            toast.classList.remove('translate-x-full', 'opacity-0');
        });
        
        // Remove after 5s
        setTimeout(() => {
            toast.classList.add('translate-x-full', 'opacity-0');
            setTimeout(() => toast.remove(), 500);
        }, 5000);
    };

    toastMessages.forEach(msg => {
        const text = msg.getAttribute('data-toast-message');
        const type = msg.getAttribute('data-toast-type');
        if (text) createToast(text, type);
    });

    // --- Room Status Toggles ---
    const statusToggles = document.querySelectorAll('[data-room-status-toggle]');
    statusToggles.forEach(toggle => {
        toggle.addEventListener('change', (e) => {
            const form = e.target.closest('form');
            const input = form.querySelector('[data-room-status-input]');
            input.value = e.target.checked ? 'Trống' : 'Bảo trì';
            form.submit();
        });
    });

    // --- Booking card: số đêm & tổng tiền (đơn giá × số đêm) ---
    const formatVnd = (amount) =>
        `${Math.round(amount).toLocaleString('en-US', { maximumFractionDigits: 0 })} VNĐ`;

    const parseYmdLocal = (value) => {
        if (!value) return null;
        const parts = value.split('-').map(Number);
        if (parts.length !== 3 || parts.some((n) => Number.isNaN(n))) return null;
        const [y, m, d] = parts;
        return new Date(y, m - 1, d);
    };

    document.querySelectorAll('[data-booking-form]').forEach((form) => {
        const card = form.closest('[data-booking-card]');
        if (!card) return;

        const priceAttr = card.getAttribute('data-price');
        const pricePerNight = priceAttr != null && priceAttr !== '' ? Number(priceAttr) : NaN;
        if (!Number.isFinite(pricePerNight) || pricePerNight < 0) return;

        const checkIn = form.querySelector('input[name="checkInDate"]');
        const checkOut = form.querySelector('input[name="checkOutDate"]');
        const nightsEl = card.querySelector('[data-nights-display]');
        const totalEl = card.querySelector('[data-total-display]');
        if (!checkIn || !checkOut || !nightsEl || !totalEl) return;

        const update = () => {
            const start = parseYmdLocal(checkIn.value);
            const end = parseYmdLocal(checkOut.value);
            let nights = 0;
            if (start && end && end > start) {
                nights = Math.round((end - start) / (1000 * 60 * 60 * 24));
            }
            const total = nights > 0 ? pricePerNight * nights : 0;
            nightsEl.textContent = nights <= 0 ? '0 đêm' : `${nights} đêm`;
            totalEl.textContent = formatVnd(total);
        };

        ['change', 'input'].forEach((evt) => {
            checkIn.addEventListener(evt, update);
            checkOut.addEventListener(evt, update);
        });
        update();
    });
});
