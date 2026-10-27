package com.example.teqgastos.ui.components

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun SeccionCamaraYGps(
    onFotoCapturada: (Uri?) -> Unit,
    onUbicacionObtenida: () -> Unit,
    ubicacionActual: String?
) {
    val context = LocalContext.current
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher 1: Capturar la foto en un archivo usando la URI generada
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            fotoUri = tempUri
            onFotoCapturada(tempUri)
            Toast.makeText(context, "Foto capturada con éxito", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher 2: Solicitar permiso de cámara en tiempo de ejecución
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                // Crear archivo temporal seguro usando FileProvider
                val photoFile = File.createTempFile(
                    "comprobante_${System.currentTimeMillis()}",
                    ".jpg",
                    context.cacheDir
                )
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    photoFile
                )
                tempUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al crear archivo de imagen: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Botón de Fotografía (Recurso Nativo 1: Cámara)
        OutlinedButton(
            onClick = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (fotoUri != null) "📷 Cambiar Foto del Comprobante" else "📷 Tomar Foto del Comprobante")
        }

        // Vista previa de la foto capturada procesando el bitmap nativamente
        fotoUri?.let { uri ->
            val bitmap = remember(uri) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                } catch (e: Exception) {
                    null
                }
            }

            bitmap?.let { bmt ->
                Image(
                    bitmap = bmt.asImageBitmap(),
                    contentDescription = "Comprobante capturado",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }

        // Botón de Ubicación GPS (Recurso Nativo 2: Geolocalización)
        OutlinedButton(
            onClick = { onUbicacionObtenida() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(ubicacionActual ?: "📍 Obtener Ubicación Actual (GPS)")
        }
    }
}