// DispositivoViewModel.kt
package br.com.fiap.gate.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import br.com.fiap.gate.models.Dispositivo
import br.com.fiap.gate.service.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DispositivoViewModel : ViewModel() {

    private val apiService = ApiService()

    // CORREÇÃO: Use um valor padrão e garanta que seja compartilhado
    private val _dispositivoAtual = MutableStateFlow<Dispositivo?>(null)
    val dispositivoAtual: StateFlow<Dispositivo?> = _dispositivoAtual

    suspend fun buscarDispositivoPorImei(imei: String): Dispositivo? {
        return withContext(Dispatchers.IO) {
            try {
                apiService.buscarDispositivoPorImei(imei)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun criarDispositivo(descricao: String, imei: String): Dispositivo? {
        return withContext(Dispatchers.IO) {
            try {
                apiService.criarDispositivo(descricao, imei)
            } catch (e: Exception) {
                null
            }
        }
    }

    // CORREÇÃO: Função para limpar o dispositivo (útil para logout)
    fun clearDispositivo() {
        _dispositivoAtual.value = null
    }

    // CORREÇÃO: Mantenha esta função para definir o dispositivo
    fun setDispositivoAtual(dispositivo: Dispositivo) {
        _dispositivoAtual.value = dispositivo
    }
}