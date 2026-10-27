package com.example.teqgastos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqgastos.domain.Gasto
import com.example.teqgastos.domain.Usuario
import com.example.teqgastos.ui.theme.*
import com.example.teqgastos.ui.viewmodel.GastoViewModel

@Composable
fun HomeScreen(
    viewModel: GastoViewModel,
    usuarioLogueado: Usuario?,
    onNuevoGastoClick: () -> Unit,
    onVerTodosGastos: () -> Unit,
    onAjustesClick: () -> Unit
) {
    val listaGastos by viewModel.gastos.collectAsState(initial = emptyList())

    val totalGastosMes = listaGastos.sumOf { it.monto }
    val cantidadPendientes = listaGastos.count { it.estado == "Pendiente" }
    val totalAprobados = listaGastos.filter { it.estado == "Aprobado" }.sumOf { it.monto }
    val topeRestante = 500000.0 - totalGastosMes

    Scaffold(
        containerColor = FondoLimpio,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoGastoClick,
                containerColor = VerdePrincipal,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nuevo Gasto", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                HeaderUsuario(
                    nombre = usuarioLogueado?.nombre ?: "Usuario",
                    cargo = if (usuarioLogueado?.rol == "JEFATURA") "Jefatura · TEQMED SpA" else "Técnico · TEQMED SpA",
                    periodo = "Octubre 2026",
                    rendicionesActivas = listaGastos.size,
                    onAjustesClick = onAjustesClick
                )
            }

            item {
                Box(modifier = Modifier.padding(16.dp)) {
                    TarjetaSincronizacion(ultimaSync = "hace 1 min", pendientesSync = cantidadPendientes)
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TarjetaMetrica(
                            titulo = "Gastos del mes",
                            valor = "$" + totalGastosMes.toInt().toString(),
                            colorFondo = VerdePrincipal,
                            colorTexto = Color.White,
                            icono = Icons.Default.ShoppingCart,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaMetrica(
                            titulo = "Pendientes",
                            valor = "$cantidadPendientes items",
                            colorFondo = Color.White,
                            colorTexto = Color.Black,
                            icono = Icons.Default.DateRange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TarjetaMetrica(
                            titulo = "Aprobados",
                            valor = "$" + totalAprobados.toInt().toString(),
                            colorFondo = Color.White,
                            colorTexto = Color.Black,
                            icono = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaMetrica(
                            titulo = "Tope restante",
                            valor = "$" + (if (topeRestante < 0) 0 else topeRestante.toInt()).toString(),
                            colorFondo = Color.White,
                            colorTexto = Color.Black,
                            icono = Icons.Default.Home,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Rendiciones recientes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onVerTodosGastos) {
                        Text("Ver todas", color = VerdePrincipal, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (listaGastos.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No tienes gastos registrados. Toca '+ Nuevo Gasto'", fontSize = 13.sp, color = TextoSecundario)
                        }
                    }
                }
            }

            items(listaGastos) { gasto ->
                ItemRendicion(gasto = gasto, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
fun HeaderUsuario(
    nombre: String,
    cargo: String,
    periodo: String,
    rendicionesActivas: Int,
    onAjustesClick: () -> Unit
) {
    val iniciales = if (nombre.isNotEmpty()) nombre.take(2).uppercase() else "US"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(VerdePrincipal)
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hola, $nombre", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(cargo, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
                IconButton(onClick = onAjustesClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TurquesaAcento),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(iniciales, fontWeight = FontWeight.Bold, color = VerdeOscuro)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(periodo, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("$rendicionesActivas rendiciones en sistema", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun TarjetaSincronizacion(ultimaSync: String, pendientesSync: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = TarjetaEstadoSync),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VerdePrincipal),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Sincronizado (Offline-First)", fontWeight = FontWeight.Bold, color = VerdeOscuro)
                Text("Última sync: $ultimaSync · $pendientesSync pendientes", fontSize = 12.sp, color = TextoSecundario)
            }
            Icon(Icons.Default.Person, contentDescription = null, tint = VerdePrincipal)
        }
    }
}

@Composable
fun TarjetaMetrica(
    titulo: String,
    valor: String,
    colorFondo: Color,
    colorTexto: Color,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(110.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icono, contentDescription = null, tint = if (colorTexto == Color.White) Color.White else VerdePrincipal)
            Column {
                Text(titulo, fontSize = 12.sp, color = if (colorTexto == Color.White) Color.White.copy(alpha = 0.8f) else TextoSecundario)
                Text(valor, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorTexto)
            }
        }
    }
}

@Composable
fun ItemRendicion(gasto: Gasto, modifier: Modifier = Modifier) {
    val colorEstado = when (gasto.estado) {
        "Aprobado" -> Color(0xFF2E7D32)
        "Rechazado" -> Color(0xFFC62828)
        "Observado" -> Color(0xFFE65100)
        else -> Color(0xFFE65100)
    }

    val fondoEstado = when (gasto.estado) {
        "Aprobado" -> Color(0xFFE8F5E9)
        "Rechazado" -> Color(0xFFFFEBEE)
        else -> Color(0xFFFFF3E0)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TarjetaEstadoSync),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = VerdePrincipal)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(gasto.concepto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${gasto.fecha} · ${gasto.proveedor}", fontSize = 12.sp, color = TextoSecundario)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$" + gasto.monto.toInt().toString(), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(fondoEstado)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(gasto.estado, fontSize = 10.sp, color = colorEstado, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}