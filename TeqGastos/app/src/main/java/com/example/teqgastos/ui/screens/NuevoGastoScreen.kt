package com.example.teqgastos.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.teqgastos.domain.Usuario
import com.example.teqgastos.ui.theme.FondoLimpio
import com.example.teqgastos.ui.theme.TarjetaEstadoSync
import com.example.teqgastos.ui.theme.TextoSecundario
import com.example.teqgastos.ui.theme.VerdePrincipal
import com.example.teqgastos.ui.viewmodel.GastoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoGastoScreen(
    viewModel: GastoViewModel,
    usuarioLogueado: Usuario?,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var montoText by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var centroCosto by remember { mutableStateOf("CC-1001 · Gran Concepción") }
    val ubicacionGps = "-36.8261, -73.0498 (Concepción)"

    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    val tomadorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoUri = Uri.parse("foto_capturada_temp")
        }
    }

    val solicitadorPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            tomadorFoto.launch(null)
        }
    }

    fun abrirCamaraSeguro() {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            tomadorFoto.launch(null)
        } else {
            solicitadorPermisoCamara.launch(Manifest.permission.CAMERA)
        }
    }

    var categoriaSeleccionada by remember { mutableStateOf("Combustible") }
    var desplegarCategorias by remember { mutableStateOf(false) }
    val listaCategorias = listOf("Combustible", "Peajes", "Alimentación", "Alojamiento", "Insumos")

    val topeCategoria = when (categoriaSeleccionada) {
        "Combustible" -> "$50.000 max / rendición"
        "Peajes" -> "$15.000 max / rendición"
        "Alimentación" -> "$25.000 max / día"
        "Alojamiento" -> "$80.000 max / noche"
        else -> "$100.000 max autorizable"
    }

    var desplegarCC by remember { mutableStateOf(false) }
    val listaCC = listOf("CC-1001 · Gran Concepción", "CC-1002 · Macrozona Sur", "CC-1003 · Urgencias TEQMED")

    val mensajeError by viewModel.mensajeError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensajeError) {
        mensajeError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registrar Nuevo Gasto", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = TarjetaEstadoSync),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = VerdePrincipal)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Ubicación de registro (GPS)", fontSize = 11.sp, color = TextoSecundario)
                        Text(ubicacionGps, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (fotoUri != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = VerdePrincipal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Comprobante adjuntado correctamente", fontWeight = FontWeight.Bold, color = VerdePrincipal)
                        }
                    } else {
                        Text("Fotografía del Comprobante (Obligatorio)", fontSize = 12.sp, color = TextoSecundario)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { abrirCamaraSeguro() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tomar Foto a la Boleta")
                        }
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = desplegarCategorias,
                onExpandedChange = { desplegarCategorias = !desplegarCategorias }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    supportingText = { Text("Tope: $topeCategoria", color = VerdePrincipal, fontWeight = FontWeight.Bold) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desplegarCategorias) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = desplegarCategorias,
                    onDismissRequest = { desplegarCategorias = false }
                ) {
                    listaCategorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                categoriaSeleccionada = cat
                                desplegarCategorias = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = montoText,
                onValueChange = { montoText = it },
                label = { Text("Monto ($)") },
                leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = proveedor,
                onValueChange = { proveedor = it },
                label = { Text("Proveedor (ej. Copec, Pronto, Shell)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = desplegarCC,
                onExpandedChange = { desplegarCC = !desplegarCC }
            ) {
                OutlinedTextField(
                    value = centroCosto,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Centro de Costo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desplegarCC) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = desplegarCC,
                    onDismissRequest = { desplegarCC = false }
                ) {
                    listaCC.forEach { cc ->
                        DropdownMenuItem(
                            text = { Text(cc) },
                            onClick = {
                                centroCosto = cc
                                desplegarCC = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.registrarGasto(
                        concepto = "$categoriaSeleccionada $proveedor",
                        montoStr = montoText,
                        categoria = categoriaSeleccionada,
                        proveedor = proveedor,
                        centroCosto = centroCosto,
                        fotoUri = fotoUri?.toString(),
                        ubicacion = ubicacionGps,
                        usuarioNombre = usuarioLogueado?.nombre ?: "Técnico Registrado",
                        onSuccess = { onVolver() }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
            ) {
                Text("Guardar y Enviar a Aprobación", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}