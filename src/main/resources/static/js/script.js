document.addEventListener("DOMContentLoaded", () => {

    fetch("navbar.html")
        .then(res => res.text())
        .then(data => {
            document.getElementById("navbar").innerHTML = data;

            initLoginModal();

            const navbar = document.querySelector(".navbar");

            const hamburger = document.querySelector(".hamburger");
            const navLinks = document.querySelector(".nav-links");
            const icon = hamburger ? hamburger.querySelector("i") : null;

            if (hamburger && navLinks && icon) {
                hamburger.addEventListener("click", () => {
                    navLinks.classList.toggle("active");

                    icon.classList.toggle("fa-bars");
                    icon.classList.toggle("fa-xmark");

                    document.body.classList.toggle("menu-open");
                });

                document.querySelectorAll(".nav-links a").forEach(link => {
                    link.addEventListener("click", () => {
                        navLinks.classList.remove("active");

                        icon.classList.remove("fa-xmark");
                        icon.classList.add("fa-bars");

                        document.body.classList.remove("menu-open");
                    });
                });
            }

            const currentPage = window.location.pathname.split("/").pop();

            document.querySelectorAll(".nav-links a").forEach(link => {
                const linkPage = link.getAttribute("href");

                if (
                    linkPage === currentPage ||
                    (currentPage === "" && linkPage === "index.html")
                ) {
                    link.classList.add("active");
                }
            });
        });

    let ticking = false;

    window.addEventListener("scroll", function () {
        if (!ticking) {
            window.requestAnimationFrame(function () {
                const navbar = document.querySelector(".navbar");

                if (window.scrollY > 5) {
                    navbar.classList.add("scrolled");
                } else {
                    navbar.classList.remove("scrolled");
                }

                ticking = false;
            });

            ticking = true;
        }
    });

    fetch("footer.html")
        .then(res => res.text())
        .then(data => {
            const footer = document.getElementById("footer");
            if (footer) footer.innerHTML = data;
        });

    const buttons = document.querySelectorAll(".filter-buttons button");
    const products = document.querySelectorAll(".product-card");

    function updateCounts() {
        buttons.forEach(button => {
            const filter = button.dataset.filter;
            let count = 0;

            products.forEach(product => {
                const category = product.dataset.category;

                if (filter === "all" || category === filter) {
                    count++;
                }
            });

            const span = button.querySelector("span");
            if (span) span.textContent = count;
        });
    }

    if (buttons.length > 0 && products.length > 0) {
        buttons.forEach(button => {
            button.addEventListener("click", () => {

                buttons.forEach(btn => btn.classList.remove("active"));
                button.classList.add("active");

                const filter = button.dataset.filter;

                products.forEach(product => {
                    const category = product.dataset.category;

                    if (filter === "all" || category === filter) {
                        product.style.display = "block";
                    } else {
                        product.style.display = "none";
                    }
                });
            });
        });

        updateCounts();
    }

    /* ================= CART LOGIC ================= */
    const cartItemsContainer = document.getElementById("cart-items");

    // Run only on cart page
    if (cartItemsContainer) {

        let cart = JSON.parse(localStorage.getItem("cart")) || [];
        let cartTotalContainer = document.getElementById("cart-total");
        let emptyCart = document.getElementById("empty-cart");

        if (cart.length === 0) {
            if (emptyCart) emptyCart.style.display = "block";
            if (cartTotalContainer) cartTotalContainer.style.display = "none";
            return;
        }

        if (emptyCart) emptyCart.style.display = "none";

        let total = 0;

        cart.forEach((item, index) => {

            total += item.price * item.quantity;

            let div = document.createElement("div");
            div.classList.add("cart-item");

            div.innerHTML = `
                <img src="${item.image}">
                <div>
                    <h3>${item.name}</h3>
                    <p>₹${item.price}</p>
                </div>
                <input type="number" value="${item.quantity}" min="1" onchange="updateQuantity(${index}, this.value)">
                <p>₹${item.price * item.quantity}</p>
                <button onclick="removeItem(${index})">Remove</button>
            `;

            cartItemsContainer.appendChild(div);
        });

        if (cartTotalContainer) {
            cartTotalContainer.innerHTML = `
                <h3>Total: ₹${total}</h3>
                <button class="btn">Proceed to Checkout</button>
            `;
        }
    }
});


/* ================= CART FUNCTIONS ================= */

function updateQuantity(index, qty) {
    let cart = JSON.parse(localStorage.getItem("cart")) || [];
    cart[index].quantity = parseInt(qty);
    localStorage.setItem("cart", JSON.stringify(cart));
    location.reload();
}

function initLoginModal() {

    // OPEN POPUP
    window.openLoginPopup = function () {
        const modal = document.getElementById("loginModal");
        if (modal) modal.style.display = "flex";
    };

    // CLOSE POPUP
    window.closeLoginPopup = function () {
        const modal = document.getElementById("loginModal");
        if (modal) modal.style.display = "none";
    };

    // TOGGLE LOGIN / SIGNUP
    window.toggleForm = function () {
        const login = document.getElementById("loginForm");
        const signup = document.getElementById("signupForm");
        const title = document.getElementById("form-title");

        if (!login || !signup || !title) return;

        login.classList.toggle("hidden");
        signup.classList.toggle("hidden");

        title.innerText = login.classList.contains("hidden")
            ? "Create Account"
            : "Welcome Back";
    };

    // CLICK OUTSIDE CLOSE
    window.addEventListener("click", function (e) {
        const modal = document.getElementById("loginModal");
        if (modal && e.target === modal) {
            modal.style.display = "none";
        }
    });
}