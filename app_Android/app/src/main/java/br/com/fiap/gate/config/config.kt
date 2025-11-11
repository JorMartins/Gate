package br.com.fiap.gate.config

// Crie um arquivo Config.kt
object Config {
    const val API_BASE_URL = "http://10.1.2.33:8123/api"

    // URLs completas
    fun getChatsUrl(idDispositivo: Long) = "$API_BASE_URL/chats/dispositivo/$idDispositivo"
    fun getMensagensUrl(idChat: Long) = "$API_BASE_URL/chat-mensagens/chat/$idChat"
    fun getEnviarMensagemUrl(idChat: Long) = "$API_BASE_URL/chat-mensagens/chat/$idChat"
}