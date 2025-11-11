// Gerenciamento de seções do menu
document.addEventListener('DOMContentLoaded', function() {
    // Mostrar seção de mensagens por padrão
    showSection('mensagens');

    // Adicionar event listeners aos botões do menu
    document.querySelectorAll('.menu-button').forEach(button => {
        button.addEventListener('click', function() {
            const section = this.getAttribute('data-section');
            
            // Atualizar estado do menu
            document.querySelectorAll('.menu-button').forEach(btn => {
                btn.classList.remove('active');
            });
            this.classList.add('active');
            
            // Mostrar seção correspondente
            showSection(section);
        });
    });

    // Carregar dados iniciais
    loadChatsForMessages();
    
    // Configurar envio de mensagens
    setupMessageSending();
    
    // Iniciar atualização automática a cada 5 segundos
    startAutoRefresh();
});

// Variáveis para controle de atualização
let autoRefreshInterval;
let lastMessagesState = {}; // Armazena o estado anterior das mensagens por chat
let chatsWithNewMessages = new Set(); // Armazena quais chats têm novas mensagens não visualizadas

function startAutoRefresh() {
    // Atualizar a cada 5 segundos
    autoRefreshInterval = setInterval(() => {
        console.log('Atualizando chats automaticamente...');
        refreshChatsAndMessages();
    }, 5000);
}

function stopAutoRefresh() {
    if (autoRefreshInterval) {
        clearInterval(autoRefreshInterval);
    }
}

async function refreshChatsAndMessages() {
    try {
        // Atualizar lista de chats
        await loadChatsForMessages();
        
        // Se há um chat selecionado, atualizar suas mensagens também
        if (selectedChatId) {
            await loadMessages(selectedChatId);
        }
    } catch (error) {
        console.error('Erro na atualização automática:', error);
    }
}

function hideAllSections() {
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.add('hidden');
    });
}

function showSection(sectionName) {
    hideAllSections();
    
    if (sectionName === 'mensagens') {
        document.getElementById('mensagens-section').classList.remove('hidden');
        loadChatsForMessages(); // Recarregar chats quando a seção for aberta
    }
}

// ===== FUNÇÕES PARA MENSAGENS =====
let selectedChatId = null;
let currentChats = []; // Armazena os chats atuais

async function loadChatsForMessages() {
    try {
        console.log('Carregando chats para mensagens...');
        const response = await fetch('/api/chats/ativos');
        
        if (response.ok) {
            const chats = await response.json();
            console.log('Chats para mensagens recebidos:', chats);
            
            // FILTRAR: Mostrar apenas chats que têm dispositivo vinculado
            const chatsComDispositivo = chats.filter(chat => chat.dispositivo !== null);
            console.log('Chats com dispositivo:', chatsComDispositivo);
            
            // Verificar se há novas mensagens comparando com o estado anterior
            checkForNewMessages(chatsComDispositivo);
            
            currentChats = chatsComDispositivo;
            renderChatsList(chatsComDispositivo);
        } else {
            console.error('Erro ao carregar chats para mensagens:', response.status);
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}

function checkForNewMessages(newChats) {
    // Para cada chat na nova lista, verifica se tem novas mensagens
    newChats.forEach(chat => {
        const chatId = chat.idChat;
        const currentMessages = chat.mensagens || [];
        const previousMessages = lastMessagesState[chatId] || [];
        
        // Verificar se há novas mensagens comparando IDs ou quantidade
        if (currentMessages.length > previousMessages.length) {
            console.log(`Novas mensagens no chat ${chatId}: ${previousMessages.length} -> ${currentMessages.length}`);
            
            // Se o chat não está selecionado no momento, marcar como tendo novas mensagens
            // E NÃO está no conjunto de chats já visualizados
            if (chatId !== selectedChatId && !chatsWithNewMessages.has(chatId)) {
                chatsWithNewMessages.add(chatId);
                chat.hasNewMessages = true;
            }
        } else if (currentMessages.length > 0 && previousMessages.length > 0) {
            // Verificar por mensagens específicas baseado no ID da última mensagem
            const lastCurrentMessage = currentMessages[currentMessages.length - 1];
            const lastPreviousMessage = previousMessages[previousMessages.length - 1];
            
            if (lastCurrentMessage.idChatMensagem !== lastPreviousMessage.idChatMensagem) {
                console.log(`Nova mensagem detectada no chat ${chatId}`);
                if (chatId !== selectedChatId && !chatsWithNewMessages.has(chatId)) {
                    chatsWithNewMessages.add(chatId);
                    chat.hasNewMessages = true;
                }
            }
        }
        
        // Atualizar o estado anterior
        lastMessagesState[chatId] = [...currentMessages];
    });
}

function renderChatsList(chats) {
    const container = document.getElementById('chatsList');
    
    // Preservar o chat selecionado atual
    const currentSelectedChatId = selectedChatId;
    
    container.innerHTML = '';

    console.log('Renderizando lista de chats com', chats.length, 'chats com dispositivo');

    if (chats.length === 0) {
        container.innerHTML = '<div class="no-messages">Nenhum chat ativo com dispositivo vinculado</div>';
        return;
    }

    chats.forEach(chat => {
        const chatItem = document.createElement('div');
        chatItem.className = 'chat-item';
        if (chat.idChat === currentSelectedChatId) {
            chatItem.classList.add('active');
        }
        
        // Verificar se este chat tem novas mensagens não visualizadas
        const hasUnreadMessages = chatsWithNewMessages.has(chat.idChat);
        
        const totalMensagens = chat.totalMensagens || chat.mensagens?.length || 0;
        const deviceName = chat.dispositivo ? chat.dispositivo.descricao : '';
        const formattedDate = formatShortDateTime(chat.dataInicial);
        
        chatItem.innerHTML = `
            <div class="chat-header">
                <div style="display: flex; align-items: center;">
                    <div class="chat-placa">${chat.placa || 'Sem placa'}</div>
                    ${deviceName ? `<div class="device-name">${deviceName}</div>` : ''}
                </div>
                ${hasUnreadMessages ? '<div class="new-message-indicator" title="Novas mensagens"></div>' : ''}
            </div>
            <div class="chat-info">
                <div class="chat-date">${formattedDate}</div>
                <div class="message-count">${totalMensagens}</div>
            </div>
        `;
        
        chatItem.addEventListener('click', () => {
            // Remover o chat do conjunto de chats com novas mensagens ao clicar
            if (chatsWithNewMessages.has(chat.idChat)) {
                chatsWithNewMessages.delete(chat.idChat);
            }
            selectChat(chat.idChat, chat.placa);
        });
        
        container.appendChild(chatItem);
    });
}

async function selectChat(chatId, placa) {
    // Remover seleção anterior
    document.querySelectorAll('.chat-item').forEach(item => {
        item.classList.remove('active');
    });
    
    // Adicionar seleção atual
    const clickedItem = event.currentTarget;
    clickedItem.classList.add('active');
    
    // Remover o indicador de novas mensagens deste chat
    if (chatsWithNewMessages.has(chatId)) {
        chatsWithNewMessages.delete(chatId);
        // Recarregar a lista para atualizar visualmente
        await loadChatsForMessages();
    }
    
    selectedChatId = chatId;
    
    // Encontrar o chat atual para pegar o nome do dispositivo
    const currentChat = currentChats.find(c => c.idChat === chatId);
    const deviceName = currentChat?.dispositivo?.descricao || '';
    
    // Atualizar título com dispositivo
    document.getElementById('selectedChatTitle').textContent = `Chat: ${placa} ${deviceName ? `- ${deviceName}` : ''} (ID: ${chatId})`;
    
    // Mostrar área de input
    document.getElementById('messageInputContainer').classList.remove('hidden');
    
    // Carregar mensagens
    await loadMessages(chatId);
}


async function loadMessages(chatId) {
    try {
        console.log(`Carregando mensagens para chat ${chatId}`);
        const response = await fetch(`/api/chat-mensagens/chat/${chatId}`);
        
        if (response.ok) {
            const mensagens = await response.json();
            console.log('Mensagens recebidas:', mensagens);
            renderMessages(mensagens);
            
            // Atualizar o estado das mensagens para este chat
            lastMessagesState[chatId] = [...mensagens];
            
            // Quando carregamos as mensagens de um chat selecionado, removemos o indicador
            if (chatsWithNewMessages.has(chatId)) {
                chatsWithNewMessages.delete(chatId);
            }
        } else {
            console.error('Erro ao carregar mensagens:', response.status);
            document.getElementById('messagesList').innerHTML = '<div class="no-messages">Erro ao carregar mensagens</div>';
        }
    } catch (error) {
        console.error('Erro:', error);
        document.getElementById('messagesList').innerHTML = '<div class="no-messages">Erro ao carregar mensagens</div>';
    }
}

function renderMessages(mensagens) {
    const container = document.getElementById('messagesList');
    container.innerHTML = '';

    console.log('Renderizando', mensagens.length, 'mensagens');

    if (mensagens.length === 0) {
        container.innerHTML = '<div class="no-messages">Nenhuma mensagem neste chat</div>';
        return;
    }

    mensagens.forEach(mensagem => {
        const messageDiv = document.createElement('div');
        const isOperadorLogistica = mensagem.operador === 'LOGISTICA';
        
        // Na LOGISTICA: LOGISTICA à direita, outras à esquerda
        let messageClass = isOperadorLogistica ? 'message-logistica' : 'message-outro';
        
        messageDiv.className = `message-item ${messageClass}`;
        messageDiv.innerHTML = `
            <div class="message-header-container">
                <div class="message-header">${mensagem.operador || 'Operador'}</div>
                <div class="message-time">${formatDateTime(mensagem.data)}</div>
            </div>
            <div class="message-text">${escapeHtml(mensagem.mensagem || '')}</div>
        `;
        
        container.appendChild(messageDiv);
    });
    
    // Rolagem para a última mensagem
    container.scrollTop = container.scrollHeight;
}

function setupMessageSending() {
    const sendButton = document.getElementById('sendMessage');
    const messageInput = document.getElementById('newMessage');
    const locationSelect = document.getElementById('locationSelect');
    
    sendButton.addEventListener('click', sendMessage);
    messageInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });
    
    // Resetar combo box quando selecionar uma localização
    locationSelect.addEventListener('change', function() {
        if (this.value !== '') {
            // Limpar o campo de mensagem quando selecionar uma localização
            messageInput.value = '';
        }
    });
}

async function sendMessage() {
    const messageInput = document.getElementById('newMessage');
    const locationSelect = document.getElementById('locationSelect');
    let message = messageInput.value.trim();
    
    if (!selectedChatId) {
        alert('Por favor, selecione um chat primeiro');
        return;
    }

    // Verificar se uma localização foi selecionada
    const selectedLocation = locationSelect.value;
    
    if (selectedLocation && selectedLocation !== '') {
        // Se tem localização selecionada, usar a mensagem padrão da localização
        const [localizacao, coordenadas] = selectedLocation.split('|');
        message = `Seguir para ${localizacao}. MAPS: ${coordenadas}`;
        
        // Resetar a combo box após o uso
        locationSelect.value = '';
    } else if (!message) {
        // Se não tem localização E não tem mensagem digitada
        alert('Por favor, digite uma mensagem ou selecione uma localização');
        return;
    }

    try {
        console.log(`Enviando mensagem para chat ${selectedChatId}: ${message}`);
        
        const response = await fetch(`/api/chat-mensagens/chat/${selectedChatId}?operador=LOGISTICA&mensagem=${encodeURIComponent(message)}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (response.ok) {
            console.log('Mensagem enviada com sucesso');
            messageInput.value = '';
            locationSelect.value = ''; // Resetar combo box
            
            // Recarregar mensagens
            await loadMessages(selectedChatId);
            
            // Atualizar a lista de chats para refletir a nova mensagem
            await loadChatsForMessages();
        } else {
            throw new Error('Erro ao enviar mensagem');
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao enviar mensagem');
    }
}

// ===== FUNÇÕES AUXILIARES =====
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDateTime(dateTimeString) {
    if (!dateTimeString) return '';
    
    try {
        const date = new Date(dateTimeString);
        return date.toLocaleString('pt-BR');
    } catch (error) {
        console.error('Erro ao formatar data:', error);
        return dateTimeString;
    }
}


function formatShortDateTime(dateTimeString) {
    if (!dateTimeString) return '';
    
    try {
        const date = new Date(dateTimeString);
        const day = date.getDate().toString().padStart(2, '0');
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        
        return `${day}/${month} - ${hours}:${minutes}`;
    } catch (error) {
        console.error('Erro ao formatar data:', error);
        return dateTimeString;
    }
}


// Limpar intervalo quando a página for fechada/recarregada
window.addEventListener('beforeunload', () => {
    stopAutoRefresh();
});
