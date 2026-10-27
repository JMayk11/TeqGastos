package com.example.teqgastos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqgastos.data.AppDatabase
import com.example.teqgastos.domain.Usuario
import com.example.teqgastos.ui.theme.FondoLimpio
import com.example.teqgastos.ui.theme.VerdePrincipal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    database: AppDatabase,
    onLoginExitoso: (Usuario) -> Unit
) {
    val scope = rememberCoroutineScope()
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var esRegistro by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf("TECNICO") }
    var mensajeError by remember { mutableStateOf("") }

    Scaffold(containerColor = FondoLimpio) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "TeqGastos",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = VerdePrincipal
            )
            Text(
                "TEQMED SpA · Sistema de Rendiciones",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        if (esRegistro) "Crear nueva cuenta" else "Iniciar Sesión",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (esRegistro) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre Completo") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Perfil:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            FilterChip(
                                selected = rolSeleccionado == "TECNICO",
                                onClick = { rolSeleccionado = "TECNICO" },
                                label = { Text("Técnico") }
                            )
                            FilterChip(
                                selected = rolSeleccionado == "JEFATURA",
                                onClick = { rolSeleccionado = "JEFATURA" },
                                label = { Text("Jefatura") }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo Corporativo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña (Mínimo 8 caracteres)") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (mensajeError.isNotEmpty()) {
                        Text(mensajeError, color = Color.Red, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            if (correo.isBlank() || password.isBlank() || (esRegistro && nombre.isBlank())) {
                                mensajeError = "Por favor completa todos los campos"
                                return@Button
                            }

                            if (password.length < 8) {
                                mensajeError = "La contraseña debe tener al menos 8 caracteres"
                                return@Button
                            }

                            scope.launch {
                                val dao = database.usuarioDao()
                                if (esRegistro) {
                                    val nuevoUsuario = Usuario(
                                        nombre = nombre,
                                        correo = correo,
                                        password = password,
                                        rol = rolSeleccionado
                                    )
                                    dao.registrarUsuario(nuevoUsuario)
                                    val usuarioInsertado = dao.login(correo, password)
                                    if (usuarioInsertado != null) {
                                        onLoginExitoso(usuarioInsertado)
                                    }
                                } else {
                                    val usuarioExistente = dao.login(correo, password)
                                    if (usuarioExistente != null) {
                                        onLoginExitoso(usuarioExistente)
                                    } else {
                                        mensajeError = "Credenciales incorrectas o usuario no registrado"
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            if (esRegistro) "Registrar y Entrar" else "Ingresar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    TextButton(
                        onClick = {
                            esRegistro = !esRegistro
                            mensajeError = ""
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            if (esRegistro) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate",
                            color = VerdePrincipal
                        )
                    }
                }
            }
        }
    }
}