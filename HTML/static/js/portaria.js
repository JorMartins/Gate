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
    loadDevices();
    loadChatsAtivos();
    loadChatsForMessages();
    
    // Configurar formulário de dispositivo
    setupDeviceForm();
    
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
    
    if (sectionName === 'dispositivos') {
        document.getElementById('dispositivos-section').classList.remove('hidden');
        loadDevices(); // Recarregar dispositivos quando a seção for aberta
    } else if (sectionName === 'chats') {
        document.getElementById('chats-section').classList.remove('hidden');
        loadChatsAtivos(); // Recarregar chats quando a seção for aberta
    } else if (sectionName === 'mensagens') {
        document.getElementById('mensagens-section').classList.remove('hidden');
        loadChatsForMessages(); // Recarregar chats quando a seção for aberta
    } else {
        document.getElementById('welcome-section').classList.remove('hidden');
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

async function selectChat(chatId, placa) {
    console.log('=== DEBUG SELECT CHAT ===');
    console.log('chatId:', chatId);
    console.log('placa:', placa);
    
    // Remover seleção anterior
    document.querySelectorAll('.chat-item').forEach(item => {
        item.classList.remove('active');
    });
    
    // Adicionar seleção atual
    event.currentTarget.classList.add('active');
    
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
    const inputContainer = document.getElementById('messageInputContainer');
    console.log('messageInputContainer found:', !!inputContainer);
    inputContainer.classList.remove('hidden');
    
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
        const isOperadorPortaria = mensagem.operador === 'PORTARIA';
        
        // Na PORTARIA: PORTARIA à direita, outras à esquerda
        let messageClass = isOperadorPortaria ? 'message-portaria' : 'message-outro';
        
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

async function sendMessage() {
    const messageInput = document.getElementById('newMessage');
    const locationSelect = document.getElementById('locationSelect');
    let message = messageInput.value.trim();
    
    console.log('=== DEBUG SEND MESSAGE START ===');
    console.log('selectedChatId:', selectedChatId);
    console.log('message input value:', messageInput.value);
    console.log('locationSelect value:', locationSelect.value);
    
    if (!selectedChatId) {
        console.log('ERROR: No chat selected');
        alert('Por favor, selecione um chat primeiro');
        return;
    }

    // Verificar se uma localização foi selecionada
    const selectedLocation = locationSelect.value;
    
    if (selectedLocation && selectedLocation !== '') {
        const [localizacao, coordenadas] = selectedLocation.split('|');
        message = `Seguir para ${localizacao}. MAPS: ${coordenadas}`;
        locationSelect.value = '';
    } else if (!message) {
        console.log('ERROR: No message and no location');
        alert('Por favor, digite uma mensagem ou selecione uma localização');
        return;
    }

    console.log('Final message to send:', message);

    try {
        const url = `/api/chat-mensagens/chat/${selectedChatId}?operador=PORTARIA&mensagem=${encodeURIComponent(message)}`;
        console.log('Fetch URL:', url);
        
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        console.log('Response status:', response.status);
        console.log('Response ok:', response.ok);
        
        if (response.ok) {
            console.log('Mensagem enviada com sucesso');
            messageInput.value = '';
            locationSelect.value = '';
            
            // Recarregar mensagens
            await loadMessages(selectedChatId);
            
            // Atualizar a lista de chats para refletir a nova mensagem
            await loadChatsForMessages();
        } else {
            const errorText = await response.text();
            console.log('Error response:', errorText);
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }
    } catch (error) {
        console.error('Erro completo:', error);
        alert('Erro ao enviar mensagem: ' + error.message);
    }
    
    console.log('=== DEBUG SEND MESSAGE END ===');
}

function setupMessageSending() {
    const sendButton = document.getElementById('sendMessage');
    const messageInput = document.getElementById('newMessage');
    const locationSelect = document.getElementById('locationSelect');
    
    console.log('=== DEBUG SETUP MESSAGE SENDING ===');
    console.log('sendButton found:', !!sendButton);
    console.log('messageInput found:', !!messageInput);
    console.log('locationSelect found:', !!locationSelect);
    
    sendButton.addEventListener('click', function() {
        console.log('Send button clicked!');
        sendMessage();
    });
    
    messageInput.addEventListener('keypress', function(e) {
        console.log('Key pressed in message input:', e.key);
        if (e.key === 'Enter') {
            console.log('Enter key detected, sending message');
            sendMessage();
        }
    });
    
    // Resetar combo box quando selecionar uma localização
    locationSelect.addEventListener('change', function() {
        console.log('Location select changed:', this.value);
        if (this.value !== '') {
            messageInput.value = '';
        }
    });
}

// ===== FUNÇÕES PARA CHATS =====
let dispositivosList = [];

async function loadChatsAtivos() {
    try {
        console.log('Carregando chats ativos...');
        const response = await fetch('/api/chats/ativos');
        
        if (response.ok) {
            const chats = await response.json();
            console.log('Chats recebidos:', chats);
            
            // Carregar dispositivos para o combobox
            await loadDispositivosForCombobox();
            renderChatsTable(chats);
        } else {
            console.error('Erro ao carregar chats:', response.status);
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}

function renderChatsTable(chats) {
    const tbody = document.getElementById('chatsTableBody');
    tbody.innerHTML = '';

    console.log('Renderizando tabela com', chats.length, 'chats');

    if (chats.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">Nenhum chat ativo</td></tr>';
        return;
    }

    chats.forEach(chat => {
        console.log('Processando chat:', chat);
        
        // REMOVENDO O IMEI - mostra apenas a descrição do dispositivo
        const dispositivoInfo = chat.dispositivo ? 
            `${chat.dispositivo.descricao}` : 
            'Não vinculado';
        
        const podeVincular = !chat.dispositivo;
        
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${chat.idChat || ''}</td>
            <td>${chat.placa || ''}</td>
            <td>${formatDateTime(chat.dataInicial) || ''}</td>
            <td>${dispositivoInfo}</td>
            <td>
                ${podeVincular ? `
                    <select class="device-combobox" id="deviceSelect-${chat.idChat}" style="padding: 5px; border: 1px solid #ddd; border-radius: 4px;">
                        <option value="">-- Selecione um dispositivo --</option>
                        ${generateDeviceOptions(dispositivosList)}
                    </select>
                    <button class="btn-edit" onclick="vincularDispositivo(${chat.idChat})" style="margin-left: 10px;">Vincular</button>
                ` : 'Já vinculado'}
            </td>
            <td>
                <button class="btn-danger" onclick="finalizarChat(${chat.idChat})">Finalizar Chat</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// ===== FUNÇÕES PARA DISPOSITIVOS =====
async function loadDevices() {
    try {
        console.log('Carregando dispositivos...');
        const response = await fetch('/api/dispositivos');
        if (response.ok) {
            const dispositivos = await response.json();
            console.log('Dispositivos recebidos:', dispositivos);
            renderDevicesTable(dispositivos);
        } else {
            console.error('Erro ao carregar dispositivos:', response.status);
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}

function renderDevicesTable(dispositivos) {
    const tbody = document.getElementById('devicesTableBody');
    tbody.innerHTML = '';

    console.log('Renderizando tabela com', dispositivos.length, 'dispositivos');

    if (dispositivos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">Nenhum dispositivo cadastrado</td></tr>';
        return;
    }

    dispositivos.forEach(device => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${device.idDispositivo}</td>
            <td>${device.descricao || ''}</td>
            <td>${device.imei || ''}</td>
            <td>
                <button class="btn-edit" onclick="editDevice(${device.idDispositivo}, '${escapeHtml(device.descricao || '')}', '${escapeHtml(device.imei || '')}')">Editar</button>
                <button class="btn-danger" onclick="deleteDevice(${device.idDispositivo})">Excluir</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function setupDeviceForm() {
    const form = document.getElementById('deviceForm');
    
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        await updateDevice();
    });
}

async function updateDevice() {
    const deviceId = document.getElementById('deviceId').value;
    const deviceData = {
        descricao: document.getElementById('deviceDescription').value,
        imei: document.getElementById('deviceImei').value
    };

    try {
        console.log('Atualizando dispositivo:', deviceId, deviceData);
        const response = await fetch(`/api/dispositivos/${deviceId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(deviceData)
        });

        if (response.ok) {
            console.log('Dispositivo atualizado com sucesso');
            cancelEdit();
            await loadDevices(); // Recarregar a lista
        } else {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao atualizar dispositivo');
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao atualizar dispositivo: ' + error.message);
    }
}

function editDevice(id, descricao, imei) {
    console.log('Editando dispositivo:', id, descricao, imei);
    
    document.getElementById('deviceId').value = id;
    document.getElementById('deviceDescription').value = descricao;
    document.getElementById('deviceImei').value = imei;
    
    document.getElementById('editForm').classList.remove('hidden');
    document.getElementById('editForm').scrollIntoView({ behavior: 'smooth' });
    document.getElementById('deviceDescription').focus();
}

function cancelEdit() {
    document.getElementById('editForm').classList.add('hidden');
    document.getElementById('deviceForm').reset();
    document.getElementById('deviceId').value = '';
}

async function deleteDevice(id) {
    if (!confirm('Tem certeza que deseja excluir este dispositivo?')) {
        return;
    }

    try {
        console.log('Excluindo dispositivo:', id);
        const response = await fetch(`/api/dispositivos/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            console.log('Dispositivo excluído com sucesso');
            await loadDevices(); // Recarregar a lista
        } else {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao excluir dispositivo');
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao excluir dispositivo: ' + error.message);
    }
}

async function loadDispositivosForCombobox() {
    try {
        const response = await fetch('/api/dispositivos');
        if (response.ok) {
            dispositivosList = await response.json();
            console.log('Dispositivos carregados para combobox:', dispositivosList.length);
        } else {
            console.error('Erro ao carregar dispositivos para combobox');
        }
    } catch (error) {
        console.error('Erro ao carregar dispositivos para combobox:', error);
    }
}

function generateDeviceOptions(dispositivos) {
    return dispositivos.map(device => 
        `<option value="${device.idDispositivo}">${device.descricao} (${device.imei})</option>`
    ).join('');
}

async function vincularDispositivo(idChat) {
    const selectElement = document.getElementById(`deviceSelect-${idChat}`);
    const idDispositivo = selectElement.value;

    if (!idDispositivo) {
        alert('Por favor, selecione um dispositivo');
        return;
    }

    if (!confirm(`Deseja vincular este chat ao dispositivo selecionado?`)) {
        return;
    }

    try {
        console.log(`Vinculando chat ${idChat} ao dispositivo ${idDispositivo}`);
        const response = await fetch(`/api/chats/${idChat}/vincular-dispositivo/${idDispositivo}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (response.ok) {
            console.log('Dispositivo vinculado com sucesso');
            await loadChatsAtivos(); // Recarregar a lista
        } else {
            throw new Error('Erro ao vincular dispositivo');
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao vincular dispositivo');
    }
}

async function finalizarChat(idChat) {
    if (!confirm('Tem certeza que deseja finalizar este chat?')) {
        return;
    }

    try {
        console.log(`Finalizando chat ${idChat}`);
        const response = await fetch(`/api/chats/${idChat}/finalizar`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (response.ok) {
            console.log('Chat finalizado com sucesso');
            await loadChatsAtivos(); // Recarregar a lista
        } else {
            throw new Error('Erro ao finalizar chat');
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao finalizar chat');
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

// Limpar intervalo quando a página for fechada/recarregada
window.addEventListener('beforeunload', () => {
    stopAutoRefresh();
});