// Mensagem.kt
package br.com.fiap.gate.models

import kotlinx.serialization.Serializable

@Serializable
data class Mensagem(
    val idMensagem: Long? = null,
    val texto: String,
    val dataEnvio: String,
    val idDispositivo: Long? = null,
    val login: String? = null
)