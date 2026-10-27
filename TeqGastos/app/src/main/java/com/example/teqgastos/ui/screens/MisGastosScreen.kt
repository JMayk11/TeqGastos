package com.example.teqgastos.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqgastos.domain.Gasto
import com.example.teqgastos.ui.theme.*
import com.example.teqgastos.ui.viewmodel.GastoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisGastosScreen(
    viewModel: GastoViewModel,
    onVolver: () -> Unit
) {
    val todosLosGastos by viewModel.gastos.collectAsState(initial = emptyList())
    var busqueda by remember { mutableStateOf("") }
    var estadoFiltro by remember { mutableStateOf("Todos") }

    val gastosFiltrados = todosLosGastos.filter { gasto ->
        val coincideBusqueda = gasto.concepto.contains(busqueda, ignoreCase = true) ||
                gasto.proveedor.contains(busqueda, ignoreCase = true) ||
                gasto.categoria.contains(busqueda, ignoreCase = true)

        val coincideEstado = when (estadoFiltro) {
            "Pendiente" -> gasto.estado == "Pendiente"
            "Aprobado" -> gasto.estado == "Aprobado"
            "Rechazado" -> gasto.estado == "Rechazado"
            "Observado" -> gasto.estado == "Observado"
            else -> true
        }

        coincideBusqueda && coincideEstado
    }

    val totalMontoFiltrado = gastosFiltrados.sumOf { it.monto }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Rendiciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        containerColor = FondoLimpio
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar por proveedor, categoría...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        IconButton(onClick = { busqueda = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )

            // Chips de filtrado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Todos", "Pendiente", "Aprobado", "Rechazado").forEach { estado ->
                    FilterChip(
                        selected = estadoFiltro == estado,
                        onClick = { estadoFiltro = estado },
                        label = { Text(estado, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VerdePrincipal,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Resumen dinámico animado
            AnimatedContent(
                targetState = totalMontoFiltrado,
                label = "AnimacionMontoTotal"
            ) { monto ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = TurquesaAcento),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${gastosFiltrados.size} resultados registrados", fontSize = 13.sp, color = VerdeOscuro)
                        Text(
                            "$${String.format("%,.0f", monto)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VerdeOscuro
                        )
                    }
                }
            }

            // Lista de items con animaciones de entrada/salida
            if (gastosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron rendiciones registradas", color = TextoSecundario)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = gastosFiltrados,
                        key = { it.id }
                    ) { gasto ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            ItemRendicion(gasto = gasto)
                        }
                    }
                }
            }
        }
    }
}