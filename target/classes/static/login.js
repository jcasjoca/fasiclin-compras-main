// Login - FasiComércio
const loginForm = document.getElementById('loginForm');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const loginBtn = document.getElementById('loginBtn');
const errorMessage = document.getElementById('errorMessage');
const successMessage = document.getElementById('successMessage');

loginForm.addEventListener('submit', function(e) {
    e.preventDefault();
    handleLogin();
});

async function handleLogin() {
    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();

    if (!username || !password) {
        showError('Por favor, preencha todos os campos.');
        return;
    }

    try {
        loginBtn.disabled = true;
        loginBtn.textContent = 'Entrando...';
        hideMessages();

        // Simular autenticação básica
        const credentials = btoa(username + ':' + password);
        
        const response = await fetch('/api/orcamentos/pendentes', {
            method: 'GET',
            headers: {
                'Authorization': 'Basic ' + credentials
            }
        });

        if (response.ok) {
            showSuccess('Login realizado com sucesso!');
            
            // Armazenar credenciais (em um cenário real, usar tokens JWT)
            sessionStorage.setItem('auth', credentials);
            
            setTimeout(() => {
                window.location.href = 'aprovacao.html';
            }, 1500);
        } else {
            throw new Error('Credenciais inválidas');
        }

    } catch (error) {
        console.error('Erro no login:', error);
        showError('Usuário ou senha incorretos. Tente usar: admin / admin');
    } finally {
        loginBtn.disabled = false;
        loginBtn.textContent = 'Login';
    }
}

function showError(message) {
    errorMessage.textContent = message;
    errorMessage.style.display = 'block';
    successMessage.style.display = 'none';
}

function showSuccess(message) {
    successMessage.textContent = message;
    successMessage.style.display = 'block';
    errorMessage.style.display = 'none';
}

function hideMessages() {
    errorMessage.style.display = 'none';
    successMessage.style.display = 'none';
}

// Verificar se já está logado
window.addEventListener('load', function() {
    const auth = sessionStorage.getItem('auth');
    if (auth) {
        showSuccess('Você já está logado!');
        setTimeout(() => {
            window.location.href = 'aprovacao.html';
        }, 1000);
    }
});