package com.example.teqgastos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqgastos.domain.Usuario
import com.example.teqgastos.ui.theme.FondoLimpio
import com.example.teqgastos.ui.theme.TextoSecundario
import com.example.teqgastos.ui.theme.VerdePrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuarioLogueado: Usuario?,
    onVolver: () -> Unit
) {
    val scrollState = rememberScrollState()
    val iniciales = usuarioLogueado?.nombre?.take(2)?.uppercase() ?: "CR"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Perfil Corporativo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = FondoLimpio
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta Principal de Identidad
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(VerdePrincipal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = iniciales,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = usuarioLogueado?.nombre ?: "Cristián Rojas",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (usuarioLogueado?.rol == "JEFATURA") "Jefatura de Servicio Técnico" else "Técnico Especialista en Terreno",
                        fontSize = 13.sp,
                        color = TextoSecundario
                    )

                    Text(
                        text = usuarioLogueado?.correo ?: "c.rojas@teqmed.cl",
                        fontSize = 12.sp,
                        color = VerdePrincipal,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Asignación Corporativa
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Información de Asignación",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = VerdePrincipal
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color.LightGray.copy(alpha = 0.4f)
                    )

                    ElementoDetallePerfil("Empresa", "TEQMED SpA")
                    Spacer(modifier = Modifier.height(10.dp))
                    ElementoDetallePerfil("Centro de Costo", "CC-1001 · Concepción")
                    Spacer(modifier = Modifier.height(10.dp))
                    ElementoDetallePerfil("Rol de Sistema", usuarioLogueado?.rol ?: "TECNICO")
                    Spacer(modifier = Modifier.height(10.dp))
                    ElementoDetallePerfil("Tope Mensual", "$500.000 CLP")
                }
            }

            // Opciones de Configuración
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Preferencias y Seguridad",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = VerdePrincipal
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color.LightGray.copy(alpha = 0.4f)
                    )

                    OpcionMenuPerfil(
                        icono = Icons.Default.Notifications,
                        titulo = "Notificaciones Push",
                        subtitulo = "Alertas de aprobación y revisiones",
                        onClick = {}
                    )

                    OpcionMenuPerfil(
                        icono = Icons.Default.Lock,
                        titulo = "Privacidad y Seguridad",
                        subtitulo = "Conforme a normativa vigente de datos",
                        onClick = {}
                    )

                    OpcionMenuPerfil(
                        icono = Icons.Default.Person,
                        titulo = "Cambiar Contraseña",
                        subtitulo = "Mínimo 8 caracteres",
                        onClick = {}
                    )

                    OpcionMenuPerfil(
                        icono = Icons.Default.Info,
                        titulo = "Acerca de TeqGastos",
                        subtitulo = "Versión 2.0 (Build 2026)",
                        onClick = {}
                    )
                }
            }

            // Botón de Cierre de Sesión
            OutlinedButton(
                onClick = onVolver,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC62828)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ElementoDetallePerfil(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = etiqueta, fontSize = 13.sp, color = TextoSecundario)
        Text(text = valor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OpcionMenuPerfil(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(VerdePrincipal.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = VerdePrincipal, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitulo, fontSize = 11.sp, color = TextoSecundario)
        }

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}