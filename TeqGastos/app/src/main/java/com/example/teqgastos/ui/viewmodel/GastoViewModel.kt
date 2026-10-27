package com.example.teqgastos.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.teqgastos.data.AppDatabase
import com.example.teqgastos.domain.Gasto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GastoViewModel(application: Application) : AndroidViewModel(application) {

    private val gastoDao = AppDatabase.getDatabase(application).gastoDao()

    val gastos = gastoDao.obtenerTodosLosGastos()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    fun limpiarError() {
        _mensajeError.value = null
    }

    fun registrarGasto(
        concepto: String,
        montoStr: String,
        categoria: String,
        proveedor: String,
        centroCosto: String,
        fotoUri: String?,
        ubicacion: String?,
        usuarioNombre: String = "Técnico Registrado",
        onSuccess: () -> Unit
    ) {
        val monto = montoStr.toDoubleOrNull()

        if (monto == null || monto <= 0) {
            _mensajeError.value = "Ingresa un monto válido mayor a 0."
            return
        }
        if (proveedor.isBlank()) {
            _mensajeError.value = "Ingresa el nombre del proveedor."
            return
        }

        val limiteCategoria = when (categoria) {
            "Alimentación" -> 25000.0
            "Combustible" -> 50000.0
            "Peajes" -> 15000.0
            "Alojamiento" -> 80000.0
            else -> 100000.0
        }

        if (monto > limiteCategoria) {
            _mensajeError.value = "El monto excede el límite de $$limiteCategoria para la categoría $categoria."
            return
        }

        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        val nuevoGasto = Gasto(
            concepto = "$categoria $proveedor",
            monto = monto,
            categoria = categoria,
            proveedor = proveedor,
            centroCosto = centroCosto,
            fecha = fechaActual,
            fotoUri = fotoUri,
            ubicacion = ubicacion,
            estado = "Pendiente",
            usuario = usuarioNombre
        )

        viewModelScope.launch {
            gastoDao.insertarGasto(nuevoGasto)
            _mensajeError.value = null
            onSuccess()
        }
    }

    fun actualizarEstadoGasto(gasto: Gasto, nuevoEstado: String) {
        viewModelScope.launch {
            val gastoActualizado = gasto.copy(estado = nuevoEstado)
            gastoDao.insertarGasto(gastoActualizado)
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GastoViewModel::class.java)) {
                return GastoViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}