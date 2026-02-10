const API = "http://localhost:8080/api";

// Register
async function registerUser() {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const pass = document.getElementById("password").value;

    const res = await fetch(`${API}/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email, password: pass })
    });

    if (res.ok) {
        alert("Registration successful!");
        window.location = "index.html";
    } else {
        alert("Registration failed");
    }
}

// Login
async function loginUser() {
    const email = document.getElementById("email").value;
    const pass = document.getElementById("password").value;

    const res = await fetch(`${API}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password: pass })
    });

    if (res.ok) {
        const user = await res.json();
        localStorage.setItem("userId", user.id);
        window.location = "dashboard.html";
    } else {
        alert("Invalid credentials");
    }
}

// Load dashboard
async function loadDashboard() {
    const userId = localStorage.getItem("userId");

    const res = await fetch(`${API}/account/balance/${userId}`);
    const data = await res.json();

    document.getElementById("accNum").innerText = data.accountNumber;
    document.getElementById("balance").innerText = data.balance;

    // FIX: Store account number
    localStorage.setItem("accNum", data.accountNumber);

    loadTransactions();
}

// Withdraw
async function withdrawMoney() {
    const amount = document.getElementById("amount").value;
    const userId = localStorage.getItem("userId");

    const res = await fetch(`${API}/account/withdraw`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId, amount })
    });

    const data = await res.json();

    if (data.error) {
        alert(data.error);
    } else {
        alert("Withdrawal successful");
        loadDashboard();
    }
}

// Load transactions
async function loadTransactions() {
    const userId = localStorage.getItem("userId");

    const res = await fetch(`${API}/account/transactions/${userId}`);
    const txList = await res.json();

    let html = "";
    if (txList.length === 0) {
        html = '<li style="border: none; color: var(--text-muted); justify-content: center;">No transactions yet</li>';
    } else {
        txList.forEach(t => {
            const date = t.createdAt ? t.createdAt.replace("T", " ").slice(0, 16) : "N/A";
            const isDeposit = t.type.toLowerCase() === 'deposit';
            const icon = isDeposit ? '↓' : '↑';
            const cls = isDeposit ? 'deposit' : 'withdrawal';

            html += `
                <li>
                    <div class="tx-icon">${icon}</div>
                    <div class="tx-details">
                        <div style="font-weight: 500;">${t.type}</div>
                        <div class="date">${date}</div>
                    </div>
                    <div class="amount ${cls}">${isDeposit ? '+' : '-'} ₹${t.amount}</div>
                </li>`;
        });
    }

    document.getElementById("txList").innerHTML = html;
}

// Deposit
async function depositMoney() {
    const amount = document.getElementById("depositAmount").value;
    const accNum = localStorage.getItem("accNum");

    if (!accNum) {
        alert("Account number missing. Reload the dashboard.");
        return;
    }

    const res = await fetch(`${API}/account/deposit/${accNum}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ amount })
    });

    const data = await res.json();

    if (data.error) {
        alert(data.error);
    } else {
        alert("Deposit successful");
        loadDashboard();
    }
}
