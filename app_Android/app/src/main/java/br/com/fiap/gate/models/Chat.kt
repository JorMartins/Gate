// Chat.kt
package br.com.fiap.gate.models

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val idChat: Long,
    val placa: String,
    val dataInicial: String,
    val dataFinal: String?,
    val dispositivo: Dispositivo,
    val mensagens: List<Mensagem> = emptyList()
)