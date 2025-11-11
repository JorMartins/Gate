package br.com.fiap.gate.models

import kotlinx.serialization.Serializable

@Serializable
data class Dispositivo(
    val idDispositivo: Long? = null,
    val descricao: String,
    val imei: String
)