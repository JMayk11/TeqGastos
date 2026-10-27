package com.example.teqgastos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqgastos.domain.Gasto
import com.example.teqgastos.domain.Usuario
import com.example.teqgastos.ui.theme.FondoLimpio
import com.example.teqgastos.ui.theme.TextoSecundario
import com.example.teqgastos.ui.theme.TurquesaAcento
import com.example.teqgastos.ui.theme.VerdeOscuro
import com.example.teqgastos.ui.theme.VerdePrincipal
import com.example.teqgastos.ui.viewmodel.GastoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AprobacionesScreen(
    viewModel: GastoViewModel,
    usuarioLogueado: Usuario?,
    onAprobarGasto: (Gasto) -> Unit,
    onRechazarGasto: (Gasto) -> Unit,
    onVolver: () -> Unit
) {
    val listaGastos by viewModel.gastos.collectAsState(initial = emptyList())
    val gastosPendientes = listaGastos.filter { it.estado == "Pendiente" }
    val scrollState = rememberScrollState()

    var gastoBoletaSeleccionado by remember { mutableStateOf<Gasto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Rendiciones por Aprobar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${gastosPendientes.size} solicitudes pendientes", fontSize = 12.sp, color = TextoSecundario)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = FondoLimpio
    ) { paddingValues ->
        if (usuarioLogueado?.rol == "TECNICO") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Acceso Restringido",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            "Tu perfil es de 'Técnico'. Solo el personal de Jefatura puede revisar y aprobar las solicitudes de rendición.",
                            fontSize = 13.sp,
                            color = TextoSecundario,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (gastosPendientes.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay rendiciones pendientes de revisión",
                                color = TextoSecundario,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    gastosPendientes.forEach { gasto ->
                        TarjetaAprobacion(
                            gasto = gasto,
                            onVerBoleta = {
                                gastoBoletaSeleccionado = gasto
                            },
                            onAprobar = {
                                onAprobarGasto(gasto)
                            },
                            onRechazar = {
                                onRechazarGasto(gasto)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (gastoBoletaSeleccionado != null) {
            val gasto = gastoBoletaSeleccionado!!
            AlertDialog(
                onDismissRequest = { gastoBoletaSeleccionado = null },
                title = {
                    Text(
                        text = "Boleta - ${gasto.concepto}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0F0F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = VerdePrincipal,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Comprobante Digitalizado",
                                    fontWeight = FontWeight.Bold,
                                    color = VerdePrincipal
                                )
                                Text(
                                    "Proveedor: ${gasto.proveedor}",
                                    fontSize = 12.sp,
                                    color = TextoSecundario
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Monto Rendido:", fontSize = 13.sp, color = TextoSecundario)
                            Text(
                                "$${gasto.monto.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = VerdePrincipal
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { gastoBoletaSeleccionado = null },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
                    ) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}

@Composable
fun TarjetaAprobacion(
    gasto: Gasto,
    onVerBoleta: () -> Unit,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit
) {
    // Nombre real extraído de la propiedad 'usuario' del gasto
    val nombreSolicitante = if (gasto.usuario.isNotBlank()) gasto.usuario else "Técnico Registrado"

    // Generación dinámica de iniciales basadas en el nombre del usuario
    val iniciales = nombreSolicitante
        .split(" ")
        .filter { it.isNotBlank() }
        .mapNotNull { it.firstOrNull() }
        .take(2)
        .joinToString("")
        .uppercase()
        .ifEmpty { "TR" }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(TurquesaAcento),
                    contentAlignment = Alignment.Center
                ) {
                    Text(iniciales, fontWeight = FontWeight.Bold, color = VerdeOscuro)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(nombreSolicitante, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("TEQMED SpA · Rendición #${gasto.id}", fontSize = 11.sp, color = TextoSecundario)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VerdePrincipal.copy(alpha = 0.1f))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = VerdePrincipal)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(gasto.concepto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${gasto.categoria} · ${gasto.fecha}", fontSize = 11.sp, color = TextoSecundario)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Proveedor", fontSize = 11.sp, color = TextoSecundario)
                    Text(gasto.proveedor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Monto", fontSize = 11.sp, color = TextoSecundario)
                    Text("$" + gasto.monto.toInt().toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = VerdePrincipal)
                }
            }

            OutlinedButton(
                onClick = onVerBoleta,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ver Boleta Fotografiada", fontSize = 12.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRechazar,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rechazar")
                }

                Button(
                    onClick = onAprobar,
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aprobar")
                }
            }
        }
    }
}