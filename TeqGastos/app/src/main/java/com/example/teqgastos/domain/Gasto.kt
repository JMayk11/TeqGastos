package com.example.teqgastos.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val concepto: String,
    val monto: Double,
    val categoria: String,
    val proveedor: String,
    val centroCosto: String,
    val fecha: String,
    val fotoUri: String? = null,
    val ubicacion: String? = null,
    val estado: String = "Pendiente",
    val usuario: String = ""
)