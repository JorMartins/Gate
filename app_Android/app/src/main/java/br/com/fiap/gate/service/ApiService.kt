package br.com.fiap.gate.service

import android.util.Log
import br.com.fiap.gate.models.Dispositivo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ApiService {

    private val baseUrl = "http://10.1.2.33:8123/api" // Ajuste para seu IP

    suspend fun buscarDispositivoPorImei(imei: String): Dispositivo? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "$baseUrl/dispositivos/imei/$imei"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    if (response.isNotEmpty() && response != "null") {
                        parseDispositivoFromJson(response)
                    } else {
                        null
                    }
                } else {
                    null // Dispositivo não encontrado
                }
            } catch (e: Exception) {
                throw Exception("Erro ao buscar dispositivo: ${e.message}")
            }
        }
    }

    // CORREÇÃO AQUI: Recebe descricao e imei como parâmetros separados
    suspend fun criarDispositivo(descricao: String, imei: String): Dispositivo? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "$baseUrl/dispositivos"
                Log.d("ApiService", "Tentando criar dispositivo na URL: $url")
                Log.d("ApiService", "Dados: descricao=$descricao, imei=$imei")

                val connection = URL(url).openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val jsonBody = """
                {
                    "descricao": "$descricao",
                    "imei": "$imei"
                }
            """.trimIndent()

                Log.d("ApiService", "JSON Body: $jsonBody")

                connection.outputStream.use { os ->
                    os.write(jsonBody.toByteArray())
                    os.flush()
                }

                val responseCode = connection.responseCode
                Log.d("ApiService", "Response Code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("ApiService", "Response Success: $response")
                    parseDispositivoFromJson(response)
                } else {
                    val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() }
                    Log.e("ApiService", "Error Response: $errorResponse")
                    throw Exception("Erro HTTP: $responseCode - $errorResponse")
                }
            } catch (e: Exception) {
                Log.e("ApiService", "Exception: ${e.message}", e)
                throw Exception("Erro ao criar dispositivo: ${e.message}")
            }
        }
    }

    private fun parseDispositivoFromJson(jsonString: String): Dispositivo {
        val jsonObject = JSONObject(jsonString)
        return Dispositivo(
            idDispositivo = if (jsonObject.has("idDispositivo")) jsonObject.getLong("idDispositivo") else null,
            descricao = if (jsonObject.has("descricao")) jsonObject.getString("descricao") else "",
            imei = jsonObject.getString("imei")
        )
    }
}