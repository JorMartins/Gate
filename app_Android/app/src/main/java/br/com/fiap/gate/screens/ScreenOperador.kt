package br.com.fiap.gate.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.fiap.gate.ui.theme.cor_beje_65

@Composable
fun ScreenOperador(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cor_beje_65),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Entrega na Spring 2",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}