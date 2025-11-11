package br.com.fiap.gate.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.fiap.gate.ui.theme.*
import android.util.Log
import androidx.compose.foundation.background
import kotlinx.coroutines.launch
import br.com.fiap.gate.viewmodels.DispositivoViewModel
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight


@Composable
fun InitialScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: DispositivoViewModel
) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCadastroButton by remember { mutableStateOf(false) }
    var deviceId by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Efeito para pegar o ID do dispositivo automaticamente
    LaunchedEffect(Unit) {
        try {
            deviceId = getDeviceId(context)
            Log.d("InitialScreen", "ID obtido: $deviceId")

            if (deviceId.isNotEmpty()) {
                // Verifica se o dispositivo existe no backend
                val dispositivo = viewModel.buscarDispositivoPorImei(deviceId)

                if (dispositivo != null) {
                    viewModel.setDispositivoAtual(dispositivo)
                    // Dispositivo encontrado - vai para a tela do cliente
                    Log.d("InitialScreen", "Dispositivo encontrado: ${dispositivo.descricao}")
                    navController.navigate("client")
                } else {
                    // Dispositivo não encontrado - mostra opção de cadastro
                    showCadastroButton = true
                }
            } else {
                errorMessage = "Não foi possível obter o ID do dispositivo"
            }
        } catch (e: Exception) {
            errorMessage = "Erro: ${e.message}"
            Log.e("InitialScreen", "Erro ao obter ID", e)
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(cor_beje_65)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = Icons.Filled.MailOutline,
            contentDescription = "Logo GATE",
            modifier = Modifier
                .size(120.dp) // tamanho opcional
                .padding(bottom = 16.dp)
        )

        Text(
            text = "GATE",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = cor_vinho_100,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = cor_vinho_100)
                Text(
                    text = "Verificando dispositivo...",
                    color = cor_vinho_75
                )
            }
        }

        if (!isLoading) {
            // Mostra o ID obtido
            Text(
                text = "ID: $deviceId",
                color = cor_vinho_100,
                modifier = Modifier.padding(bottom = 16.dp),
                fontSize = 12.sp
            )

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 14.sp
                )
            }

            // Botão de SOLICITAR CADASTRO (quando dispositivo não existe)
            if (showCadastroButton) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Este dispositivo ainda não está cadastrado",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )

                    // Campo para digitar a descrição
                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = { Text("Descrição do dispositivo", color = cor_vinho_100) },
                        placeholder = { Text("Ex: Celular João, Tablet Maria, etc.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cor_vinho_100,
                            unfocusedBorderColor = cor_vinho_75,
                            focusedLabelColor = cor_vinho_100,
                            unfocusedLabelColor = cor_vinho_75,
                            focusedTextColor = cor_black,
                            unfocusedTextColor = cor_black,
                            cursorColor = cor_vinho_100
                        )
                    )

                    Button(
                        onClick = {
                            if (descricao.isBlank()) {
                                errorMessage = "Digite uma descrição para o dispositivo"
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                try {
                                    Log.d("InitialScreen", "Tentando cadastrar dispositivo: $deviceId")
                                    // Cadastra o dispositivo com a descrição informada
                                    val novoDispositivo = viewModel.criarDispositivo(
                                        descricao = descricao,
                                        imei = deviceId
                                    )

                                    if (novoDispositivo != null) {
                                        viewModel.setDispositivoAtual(novoDispositivo)
                                        Log.d("InitialScreen", "Dispositivo cadastrado com sucesso: ${novoDispositivo.idDispositivo}")
                                        navController.navigate("client")
                                    } else {
                                        errorMessage = "Erro ao cadastrar dispositivo - tente novamente"
                                        Log.e("InitialScreen", "Dispositivo retornou null")
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Erro: ${e.message}"
                                    Log.e("InitialScreen", "Erro ao cadastrar", e)
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cor_vinho_75,
                            contentColor = cor_white
                        ),
                        enabled = descricao.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = cor_white,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Solicitar Cadastro", fontSize = 16.sp)
                        }
                    }

                }
            }

            // Botão para tentar novamente em caso de erro
            if (errorMessage != null && !showCadastroButton) {
                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        showCadastroButton = false
                        coroutineScope.launch {
                            try {
                                deviceId = getDeviceId(context)
                                if (deviceId.isNotEmpty()) {
                                    val dispositivo = viewModel.buscarDispositivoPorImei(deviceId)
                                    if (dispositivo != null) {
                                        navController.navigate("client")
                                    } else {
                                        showCadastroButton = true
                                    }
                                }
                            } catch (e: Exception) {
                                errorMessage = "Erro: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cor_vinho_100,
                        contentColor = cor_white
                    )
                ) {
                    Text("Tentar Novamente", fontSize = 16.sp)
                }
            }
        }
    }
}

// Função simplificada para obter um ID único do dispositivo
private fun getDeviceId(context: Context): String {
    return try {
        // Usa o Android ID (não requer permissão)
        val androidId = android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )

        if (androidId != null && androidId != "9774d56d682e549c" && androidId.length > 5) {
            // Android ID válido encontrado
            "ANDROID_${androidId}"
        } else {
            // Gera um ID único persistente
            val sharedPrefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
            var deviceId = sharedPrefs.getString("device_id", null)

            if (deviceId == null) {
                deviceId = "DEVICE_${System.currentTimeMillis()}_${(1000..9999).random()}"
                sharedPrefs.edit().putString("device_id", deviceId).apply()
            }

            deviceId
        }
    } catch (e: Exception) {
        Log.e("getDeviceId", "Erro ao obter ID do dispositivo", e)
        "TEMP_${System.currentTimeMillis()}"
    }
}