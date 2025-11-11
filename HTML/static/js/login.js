document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const loginBtn = document.getElementById('loginBtn');
    const loading = document.getElementById('loading');
    const errorMessage = document.getElementById('errorMessage');
    
    // Reset estados
    errorMessage.style.display = 'none';
    loginBtn.disabled = true;
    loading.style.display = 'block';
    
    try {
        console.log(`Verificando usuário: ${username}`);
        
        // Primeiro, verifica se o usuário existe via nossa rota proxy
        const existeResponse = await fetch(`/api/usuarios/existe/${username}`);
        console.log(`Status da resposta: ${existeResponse.status}`);
        
        if (!existeResponse.ok) {
            throw new Error('Erro ao verificar usuário');
        }
        
        const existeData = await existeResponse.json();
        console.log('Dados recebidos:', existeData);
        
        const usuarioExiste = existeData.existe;
        console.log(`Usuário existe: ${usuarioExiste}`);
        
        if (!usuarioExiste) {
            throw new Error('Usuário não encontrado no sistema');
        }
        
        console.log('Usuário encontrado, autenticando...');
        
        // Se usuário existe, faz autenticação
        const authResponse = await fetch('/auth', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`
        });
        
        console.log(`Status da autenticação: ${authResponse.status}`);
        
        if (authResponse.redirected) {
            // Se houve redirecionamento, segue para a nova URL
            window.location.href = authResponse.url;
        } else if (authResponse.ok) {
            // Se não houve redirecionamento mas a resposta foi OK
            window.location.reload();
        } else {
            throw new Error('Senha inválida');
        }
        
    } catch (error) {
        console.error('Erro no login:', error);
        errorMessage.textContent = error.message;
        errorMessage.style.display = 'block';
    } finally {
        loginBtn.disabled = false;
        loading.style.display = 'none';
    }
});