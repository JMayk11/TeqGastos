package com.example.teqgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.teqgastos.data.AppDatabase
import com.example.teqgastos.domain.Usuario
import com.example.teqgastos.ui.components.BottomNavigationBar
import com.example.teqgastos.ui.components.Pantalla
import com.example.teqgastos.ui.screens.*
import com.example.teqgastos.ui.theme.TeqGastosTheme
import com.example.teqgastos.ui.viewmodel.GastoViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val gastoViewModel: GastoViewModel by viewModels {
        GastoViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)

        setContent {
            var esModoOscuro by remember { mutableStateOf(false) }
            var factorEscalaTexto by remember { mutableFloatStateOf(1.0f) }
            var usuarioLogueado by remember { mutableStateOf<Usuario?>(null) }

            TeqGastosTheme(darkTheme = esModoOscuro) {
                val densidadActual = LocalDensity.current
                val nuevaDensidad = Density(
                    density = densidadActual.density,
                    fontScale = factorEscalaTexto
                )

                CompositionLocalProvider(LocalDensity provides nuevaDensidad) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: "login"

                    val pantallaActualEnum = when (currentRoute) {
                        "mis_gastos" -> Pantalla.MIS_GASTOS
                        "aprobaciones" -> Pantalla.APROBACIONES
                        "perfil" -> Pantalla.PERFIL
                        else -> Pantalla.INICIO
                    }

                    Scaffold(
                        bottomBar = {
                            if (currentRoute in listOf("home", "mis_gastos", "aprobaciones", "perfil")) {
                                BottomNavigationBar(
                                    pantallaActual = pantallaActualEnum,
                                    onPantallaSeleccionada = { pantalla ->
                                        val rutaDestino = when (pantalla) {
                                            Pantalla.INICIO -> "home"
                                            Pantalla.MIS_GASTOS -> "mis_gastos"
                                            Pantalla.APROBACIONES -> "aprobaciones"
                                            Pantalla.PERFIL -> "perfil"
                                        }
                                        navController.navigate(rutaDestino) {
                                            popUpTo("home") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = "login",
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable("login") {
                                LoginScreen(
                                    database = database,
                                    onLoginExitoso = { usuario ->
                                        usuarioLogueado = usuario
                                        if (usuario.rol == "JEFATURA") {
                                            navController.navigate("aprobaciones") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }
                            composable("home") {
                                HomeScreen(
                                    viewModel = gastoViewModel,
                                    usuarioLogueado = usuarioLogueado,
                                    onNuevoGastoClick = { navController.navigate("nuevo_gasto") },
                                    onVerTodosGastos = { navController.navigate("mis_gastos") },
                                    onAjustesClick = { navController.navigate("ajustes") }
                                )
                            }
                            composable("nuevo_gasto") {
                                NuevoGastoScreen(
                                    viewModel = gastoViewModel,
                                    usuarioLogueado = usuarioLogueado,
                                    onVolver = { navController.popBackStack() }
                                )
                            }
                            composable("aprobaciones") {
                                val scope = rememberCoroutineScope()

                                AprobacionesScreen(
                                    viewModel = gastoViewModel,
                                    usuarioLogueado = usuarioLogueado,
                                    onAprobarGasto = { gasto ->
                                        scope.launch {
                                            database.gastoDao().insertarGasto(gasto.copy(estado = "Aprobado"))
                                        }
                                    },
                                    onRechazarGasto = { gasto ->
                                        scope.launch {
                                            database.gastoDao().insertarGasto(gasto.copy(estado = "Rechazado"))
                                        }
                                    },
                                    onVolver = { navController.popBackStack() }
                                )
                            }
                            composable("mis_gastos") {
                                MisGastosScreen(
                                    viewModel = gastoViewModel,
                                    onVolver = { navController.popBackStack() }
                                )
                            }
                            composable("perfil") {
                                PerfilScreen(
                                    usuarioLogueado = usuarioLogueado,
                                    onVolver = { navController.popBackStack() }
                                )
                            }
                            composable("ajustes") {
                                AjustesScreen(
                                    esModoOscuro = esModoOscuro,
                                    onModoOscuroChange = { esModoOscuro = it },
                                    factorEscalaTexto = factorEscalaTexto,
                                    onFactorEscalaChange = { factorEscalaTexto = it },
                                    onCerrarSesion = {
                                        usuarioLogueado = null
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    onVolver = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}