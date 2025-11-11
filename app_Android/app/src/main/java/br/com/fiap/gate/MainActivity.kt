// MainActivity.kt
package br.com.fiap.gate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.fiap.gate.screens.InitialScreen
import br.com.fiap.gate.screens.ScreenClient
import br.com.fiap.gate.screens.ScreenOperador
import br.com.fiap.gate.ui.theme.ReciclaAquiTheme
import br.com.fiap.gate.viewmodels.DispositivoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReciclaAquiTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val navController = rememberNavController()
                    val viewModel: DispositivoViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(route = "login") {
                            InitialScreen(
                                navController = navController,
                                viewModel = viewModel // Passe o ViewModel
                            )
                        }
                        composable(route = "client") {
                            ScreenClient(
                                //navController = navController,
                                viewModel = viewModel // Passe o ViewModel
                            )
                        }
                        composable(route = "operador") {
                            ScreenOperador(navController = navController)
                        }
                    }
                }
            }
        }
    }
}