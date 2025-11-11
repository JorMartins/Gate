package br.com.fiap.gate.screens

import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.gate.config.Config
import br.com.fiap.gate.models.Dispositivo
import br.com.fiap.gate.ui.theme.*
import kotlinx.coroutines.launch
import br.com.fiap.gate.viewmodels.DispositivoViewModel
import java.text.SimpleDateFormat
import java.util.*
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import androidx.compose.material.icons.filled.PlayArrow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenClient(
    viewModel: DispositivoViewModel
) {
    var isLoading by remember { mutableStateOf(true) }
    var chats by remember { mutableStateOf<List<Chat>>(emptyList()) }
    var mensagens by remember { mutableStateOf<List<Mensagem>>(emptyList()) }
    var chatSelecionado by remember { mutableStateOf<Chat?>(null) }
    var novaMensagem by remember { mutableStateOf("") }
    var showExitDialog by remember { mutableStateOf(false) }
    var atualizacaoAtiva by remember { mutableStateOf(true) }
    var contagemMensagensPorChat by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }
    var chatsComNovasMensagens by remember { mutableStateOf<Set<Long>>(emptySet()) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val dispositivoState by viewModel.dispositivoAtual.collectAsState()
    val dispositivo = dispositivoState

    fun scrollToBottom() {
        coroutineScope.launch {
            if (mensagens.isNotEmpty()) {
                listState.animateScrollToItem(mensagens.size - 1)
            }
        }
    }

    fun buscarMensagensComDetecao(idChat: Long, callback: (List<Mensagem>) -> Unit) {
        buscarMensagensDoChat(idChat) { novasMensagens ->
            val contagemAtual = contagemMensagensPorChat[idChat] ?: 0

            if (novasMensagens.size > contagemAtual && idChat != chatSelecionado?.idChat) {
                chatsComNovasMensagens = chatsComNovasMensagens + idChat
            }

            callback(novasMensagens)
        }
    }


    fun atualizarTudo() {
        dispositivo?.idDispositivo?.let { id ->
            buscarChatsDoDispositivo(id) { novosChats ->
                chats = novosChats

                novosChats.forEach { chat ->
                    buscarMensagensComDetecao(chat.idChat) { novasMensagens ->
                        if (chat.idChat == chatSelecionado?.idChat) {
                            mensagens = novasMensagens
                            scrollToBottom()
                        }
                    }
                }

                if (chatSelecionado == null && novosChats.isNotEmpty()) {
                    chatSelecionado = novosChats.first()
                }
            }
        }
    }


    LaunchedEffect(dispositivo) {
        if (dispositivo != null) {
            dispositivo.idDispositivo?.let { id ->
                buscarChatsDoDispositivo(id) { listaChats ->
                    chats = listaChats
                    isLoading = false

                    val contagemInicial = mutableMapOf<Long, Int>()
                    listaChats.forEach { chat ->
                        buscarMensagensDoChat(chat.idChat) { mensagensChat ->
                            contagemInicial[chat.idChat] = mensagensChat.size
                            contagemMensagensPorChat = contagemInicial

                            if (chat.idChat == chatSelecionado?.idChat) {
                                mensagens = mensagensChat
                                scrollToBottom()
                            }
                        }
                    }

                    if (listaChats.isNotEmpty() && chatSelecionado == null) {
                        chatSelecionado = listaChats.first()
                    }
                }
            }
        } else {
            isLoading = false
        }
    }

    LaunchedEffect(chatSelecionado) {
        chatSelecionado?.let { chat ->

            if (chatsComNovasMensagens.contains(chat.idChat)) {
                chatsComNovasMensagens = chatsComNovasMensagens - chat.idChat
            }

            buscarMensagensDoChat(chat.idChat) { listaMensagens ->
                mensagens = listaMensagens
                scrollToBottom()
            }
        }
    }

    // CORREÇÃO: Efeito para ATUALIZAÇÃO AUTOMÁTICA - Versão simplificada e funcional
    LaunchedEffect(atualizacaoAtiva) {
        if (!atualizacaoAtiva) return@LaunchedEffect

        while (atualizacaoAtiva && dispositivo != null) {
            try {
                kotlinx.coroutines.delay(5000) // Aguarda 5 segundos

                if (!atualizacaoAtiva) break

                atualizarTudo()

            } catch (e: Exception) {
            }
        }
    }

    LaunchedEffect(mensagens) {
        if (mensagens.isNotEmpty()) {
            scrollToBottom()
        }
    }

    // Alerta de confirmação para sair
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Sair do Aplicativo") },
            text = { Text("Deseja realmente sair do aplicativo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        (context as? android.app.Activity)?.finishAffinity()
                    }
                ) {
                    Text("SIM", color = cor_vinho_75)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text("NÃO", color = cor_vinho_100)
                }
            }
        )
    }

    // LAYOUT PRINCIPAL
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cor_beje_65)
    ) {
        // CABEÇALHO
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dispositivo?.descricao ?: "Dispositivo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = cor_vinho_100
                    )
                    Text(
                        text = "${chats.size} conversas | Novas: ${chatsComNovasMensagens.size}",
                        fontSize = 12.sp,
                        color = cor_vinho_75
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status da atualização automática
                    Text(
                        text = if (atualizacaoAtiva) "ATIVA" else "PAUSADA",
                        fontSize = 12.sp,
                        color = if (atualizacaoAtiva) Color.Green else Color.Red,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    // Botão para pausar/retomar atualização automática
                    IconButton(
                        onClick = {
                            atualizacaoAtiva = !atualizacaoAtiva
                            if (atualizacaoAtiva) {
                                atualizarTudo()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (atualizacaoAtiva) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                            contentDescription = if (atualizacaoAtiva) "Pausar atualização automática" else "Retomar atualização automática",
                            tint = if (atualizacaoAtiva) Color.Green else Color.Red
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { showExitDialog = true },
                        colors = ButtonDefaults.buttonColors(cor_vinho_100)
                    ) {
                        Text("Sair", color = Color.White)
                    }
                }
            }
        }

        // CONTEÚDO PRINCIPAL
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = cor_vinho_100)
                    Text(
                        text = "Carregando conversas...",
                        color = cor_vinho_75,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                // COLUNA DA ESQUERDA - LISTA DE CONVERSAS
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.White)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Conversas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = cor_vinho_100,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                    )

                    if (chats.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                chats,
                                key = { chat -> chat.idChat }
                            ) { chat ->
                                val isSelected = chatSelecionado?.idChat == chat.idChat
                                val temNovaMensagem = chatsComNovasMensagens.contains(chat.idChat)


                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            chatSelecionado = chat
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = when {
                                            isSelected -> cor_beje_65
                                            // JORGESSS  temNovaMensagem -> Color(0xFFE8F5E8)
                                            else -> Color.White
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = chat.placa,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = cor_vinho_100
                                            )
                                            Text(
                                                text = "Iniciado: ${formatarData(chat.dataInicial)}",
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }

                                        // Indicador de nova mensagem
                                        if (temNovaMensagem) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .background(Color.Green, shape = CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma conversa disponível",
                                fontSize = 14.sp,
                                color = cor_vinho_75
                            )
                        }
                    }
                }

                // COLUNA DA DIREITA - MENSAGENS DO CHAT SELECIONADO
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .background(Color.White)
                ) {
                    if (chatSelecionado != null) {
                        // Header do chat
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(containerColor = cor_beje_65)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Chat: ${chatSelecionado!!.placa}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = cor_vinho_100
                                )

                                // Indicador se há novas mensagens neste chat
                                if (chatsComNovasMensagens.contains(chatSelecionado!!.idChat)) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color.Green, shape = CircleShape)
                                    )
                                }
                            }
                        }

                        // Lista de mensagens
                        if (mensagens.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                state = listState
                            ) {
                                items(mensagens) { mensagem ->
                                    val isOperadorOutros =
                                        mensagem.operador.equals("PORTARIA", ignoreCase = true) ||
                                                mensagem.operador.equals("LOGISTICA", ignoreCase = true) ||
                                                mensagem.operador.equals("FATURAMENTO", ignoreCase = true)

                                    val alinhamento = if (isOperadorOutros) Arrangement.Start else Arrangement.End
                                    val corFundo = if (isOperadorOutros) cor_beje_25 else cor_beje_50

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = alinhamento
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .widthIn(max = 280.dp)
                                                .padding(horizontal = 8.dp),
                                            horizontalAlignment = if (isOperadorOutros) Alignment.Start else Alignment.End
                                        ) {
                                            Text(
                                                text = "${mensagem.operador ?: "Sistema"} • ${formatarApenasHorario(mensagem.data ?: mensagem.dataEnvio ?: "")}",
                                                fontSize = 10.sp,
                                                color = if (isOperadorOutros) cor_vinho_75 else cor_vinho_100,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )

                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = corFundo),
                                                elevation = CardDefaults.cardElevation(2.dp)
                                            ) {
                                                // VERIFICA SE A MENSAGEM CONTÉM COORDENADAS PARA MAPA
                                                val textoMensagem = mensagem.mensagem ?: mensagem.texto ?: ""

                                                if (contemCoordenadasMaps(textoMensagem)) {
                                                    // Extrai as coordenadas e cria link para Google Maps
                                                    val (textoPrincipal, coordenadas) = extrairCoordenadasMaps(textoMensagem)

                                                    Column(
                                                        modifier = Modifier.padding(12.dp)
                                                    ) {
                                                        if (textoPrincipal.isNotBlank()) {
                                                            Text(
                                                                text = textoPrincipal,
                                                                fontSize = 14.sp,
                                                                color = Color.Black,
                                                                modifier = Modifier.padding(bottom = 8.dp)
                                                            )
                                                        }

                                                        // Link clicável para o Google Maps
                                                        Text(
                                                            text = "📍 Abrir no Google Maps",
                                                            fontSize = 14.sp,
                                                            color = Color.Blue,
                                                            modifier = Modifier
                                                                .clickable {
                                                                    abrirGoogleMaps(coordenadas, context)
                                                                }
                                                                .padding(4.dp)
                                                        )
                                                    }
                                                } else {
                                                    Text(
                                                        text = textoMensagem,
                                                        fontSize = 14.sp,
                                                        color = Color.Black,
                                                        modifier = Modifier.padding(12.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Nenhuma mensagem neste chat",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "Envie a primeira mensagem!",
                                        fontSize = 12.sp,
                                        color = Color.LightGray,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }

                        // Input de mensagem
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicTextField(
                                    value = novaMensagem,
                                    onValueChange = { novaMensagem = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(cor_beje_25)
                                        .padding(12.dp),
                                    decorationBox = { innerTextField ->
                                        if (novaMensagem.isEmpty()) {
                                            Text(
                                                text = "Digite sua mensagem...",
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                )

                                IconButton(
                                    onClick = {
                                        if (novaMensagem.isNotBlank() && chatSelecionado != null && dispositivo != null) {
                                            val mensagemTrim = novaMensagem.trim()
                                            val operador = dispositivo.descricao ?: "Tablet"

                                            enviarMensagem(
                                                idChat = chatSelecionado!!.idChat,
                                                operador = operador,
                                                mensagem = mensagemTrim
                                            ) { sucesso ->
                                                if (sucesso) {
                                                    novaMensagem = ""
                                                    // Atualiza as mensagens após enviar
                                                    buscarMensagensDoChat(chatSelecionado!!.idChat) { novasMensagens ->
                                                        mensagens = novasMensagens
                                                        scrollToBottom()
                                                    }
                                                    Log.d("ScreenClient", "✅ Mensagem enviada com sucesso")
                                                } else {
                                                    Log.e("ScreenClient", "❌ Falha ao enviar mensagem")
                                                }
                                            }
                                        }
                                    },
                                    enabled = novaMensagem.isNotBlank()
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Enviar",
                                        tint = if (novaMensagem.isNotBlank()) cor_vinho_100 else Color.Gray
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Selecione uma conversa",
                                    fontSize = 16.sp,
                                    color = cor_vinho_75,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Escolha uma conversa na coluna ao lado para ver as mensagens",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Função para verificar se a mensagem contém coordenadas do Maps
private fun contemCoordenadasMaps(mensagem: String): Boolean {
    return mensagem.contains("MAPS:", ignoreCase = true)
}

// Função para extrair coordenadas e texto da mensagem
private fun extrairCoordenadasMaps(mensagem: String): Pair<String, String> {
    return try {
        val parts = mensagem.split("MAPS:", ignoreCase = true)
        val textoPrincipal = parts[0].trim()
        val coordenadas = parts[1].trim()
        Pair(textoPrincipal, coordenadas)
    } catch (e: Exception) {
        Pair(mensagem, "")
    }
}

// Função para abrir o Google Maps
private fun abrirGoogleMaps(coordenadas: String, context: android.content.Context) {
    try {
        // Remove espaços e formata as coordenadas
        val coordsFormatadas = coordenadas.replace(" ", "").replace(",", ",")
        val uri = "geo:$coordsFormatadas?q=$coordsFormatadas"
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        context.startActivity(intent)
    } catch (e: Exception) {
        // Se o Google Maps não estiver instalado, abre no browser
        try {
            val coordsFormatadas = coordenadas.replace(" ", "").replace(",", ",")
            val uri = "https://www.google.com/maps/search/?api=1&query=$coordsFormatadas"
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))
            context.startActivity(intent)
        } catch (e2: Exception) {
            Log.e("ScreenClient", "❌ Erro ao abrir maps: ${e2.message}")
        }
    }
}

// DATA CLASSES (mantidas iguais)
data class Mensagem(
    val idMensagem: Long? = null,
    val idChatMensagem: Long? = null,
    val texto: String? = null,
    val mensagem: String? = null,
    val dataEnvio: String? = null,
    val data: String? = null,
    val idDispositivo: Long? = null,
    val login: String? = null,
    val operador: String? = null,
    val timestamp: Long? = null
)

data class Chat(
    val idChat: Long,
    val placa: String,
    val dataInicial: String,
    val dataFinal: String?,
    val dispositivo: Dispositivo
)


private fun parseMensagensFromJson(jsonString: String): List<Mensagem> {
    val mensagens = mutableListOf<Mensagem>()
    try {
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val mensagemObj = jsonArray.getJSONObject(i)
            val mensagem = Mensagem(
                idChatMensagem = if (mensagemObj.has("idChatMensagem")) mensagemObj.getLong("idChatMensagem") else null,
                texto = if (mensagemObj.has("texto")) mensagemObj.getString("texto") else null,
                mensagem = if (mensagemObj.has("mensagem")) mensagemObj.getString("mensagem") else null,
                dataEnvio = if (mensagemObj.has("dataEnvio")) mensagemObj.getString("dataEnvio") else null,
                data = if (mensagemObj.has("data")) mensagemObj.getString("data") else null,
                idDispositivo = if (mensagemObj.has("idDispositivo")) mensagemObj.getLong("idDispositivo") else null,
                login = if (mensagemObj.has("login")) mensagemObj.getString("login") else null,
                operador = if (mensagemObj.has("operador")) mensagemObj.getString("operador") else null,
                timestamp = if (mensagemObj.has("data")) {
                    converterDataParaTimestamp(mensagemObj.getString("data"))
                } else if (mensagemObj.has("dataEnvio")) {
                    converterDataParaTimestamp(mensagemObj.getString("dataEnvio"))
                } else {
                    null
                }
            )
            mensagens.add(mensagem)
        }
    } catch (e: Exception) {
        Log.e("ScreenClient", "❌ Erro ao fazer parse das mensagens: ${e.message}")
    }
    return mensagens
}

private fun converterDataParaTimestamp(dataString: String?): Long? {
    if (dataString.isNullOrBlank()) return null
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val date = inputFormat.parse(dataString)
        date?.time
    } catch (e: Exception) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dataString)
            date?.time
        } catch (e2: Exception) {
            null
        }
    }
}

private fun parseChatsFromJson(jsonString: String): List<Chat> {
    val chats = mutableListOf<Chat>()
    try {
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val idChat = jsonObject.getLong("idChat")
            val placa = jsonObject.getString("placa")
            val dataInicial = jsonObject.getString("dataInicial")
            val dataFinal = if (jsonObject.has("dataFinal") && !jsonObject.isNull("dataFinal")) jsonObject.getString("dataFinal") else null

            val dispositivoObj = jsonObject.getJSONObject("dispositivo")
            val dispositivo = Dispositivo(
                idDispositivo = if (dispositivoObj.has("idDispositivo")) dispositivoObj.getLong("idDispositivo") else null,
                descricao = if (dispositivoObj.has("descricao")) dispositivoObj.getString("descricao") else "",
                imei = if (dispositivoObj.has("imei")) dispositivoObj.getString("imei") else ""
            )

            chats.add(Chat(idChat, placa, dataInicial, dataFinal, dispositivo))
        }
    } catch (e: Exception) {
        Log.e("ScreenClient", "❌ Erro ao fazer parse do JSON: ${e.message}")
    }
    return chats
}

private fun buscarChatsDoDispositivo(idDispositivo: Long, callback: (List<Chat>) -> Unit) {
    Thread {
        try {
            val url = URL(Config.getChatsUrl(idDispositivo))
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val chats = parseChatsFromJson(response)
                android.os.Handler(android.os.Looper.getMainLooper()).post { callback(chats) }
            } else {
                Log.e("ScreenClient", "❌ Erro HTTP ao buscar chats: $responseCode")
                android.os.Handler(android.os.Looper.getMainLooper()).post { callback(emptyList()) }
            }
        } catch (e: Exception) {
            Log.e("ScreenClient", "❌ Erro ao buscar chats: ${e.message}")
            android.os.Handler(android.os.Looper.getMainLooper()).post { callback(emptyList()) }
        }
    }.start()
}

private fun buscarMensagensDoChat(idChat: Long, callback: (List<Mensagem>) -> Unit) {
    Thread {
        try {
            val url = URL(Config.getMensagensUrl(idChat))
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val mensagens = parseMensagensFromJson(response)
                android.os.Handler(android.os.Looper.getMainLooper()).post { callback(mensagens) }
            } else {
                Log.e("ScreenClient", "❌ Erro HTTP ao buscar mensagens: $responseCode")
                android.os.Handler(android.os.Looper.getMainLooper()).post { callback(emptyList()) }
            }
        } catch (e: Exception) {
            Log.e("ScreenClient", "❌ Erro ao buscar mensagens: ${e.message}")
            android.os.Handler(android.os.Looper.getMainLooper()).post { callback(emptyList()) }
        }
    }.start()
}

private fun enviarMensagem(idChat: Long, operador: String, mensagem: String, callback: (Boolean) -> Unit) {
    Thread {
        try {
            val url = URL(Config.getEnviarMensagemUrl(idChat))
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.doOutput = true
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val parametros = "operador=${URLEncoder.encode(operador, "UTF-8")}&mensagem=${URLEncoder.encode(mensagem.trim(), "UTF-8")}"

            connection.outputStream.use { os ->
                os.write(parametros.toByteArray())
                os.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                android.os.Handler(android.os.Looper.getMainLooper()).post { callback(true) }
            } else {
                Log.e("ScreenClient", "❌ Erro HTTP ao enviar: $responseCode")
                android.os.Handler(android.os.Looper.getMainLooper()).post { callback(false) }
            }
        } catch (e: Exception) {
            Log.e("ScreenClient", "❌ Erro ao enviar mensagem: ${e.message}")
            android.os.Handler(android.os.Looper.getMainLooper()).post { callback(false) }
        }
    }.start()
}

private fun formatarData(dataString: String?): String {
    if (dataString.isNullOrBlank()) return ""
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val date = inputFormat.parse(dataString)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dataString)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            outputFormat.format(date)
        } catch (e2: Exception) {
            dataString
        }
    }
}

private fun formatarApenasHorario(dataString: String?): String {
    if (dataString.isNullOrBlank()) return ""
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val date = inputFormat.parse(dataString)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dataString)
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            outputFormat.format(date)
        } catch (e2: Exception) {
            ""
        }
    }
}