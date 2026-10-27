package com.example.teqgastos.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.teqgastos.ui.theme.TurquesaAcento
import com.example.teqgastos.ui.theme.VerdeOscuro

enum class Pantalla(val titulo: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Default.Home),
    MIS_GASTOS("Mis Gastos", Icons.Default.DateRange),
    APROBACIONES("Aprobaciones", Icons.Default.CheckCircle),
    PERFIL("Perfil", Icons.Default.Person)
}

@Composable
fun BottomNavigationBar(
    pantallaActual: Pantalla,
    onPantallaSeleccionada: (Pantalla) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        Pantalla.entries.forEach { pantalla ->
            val seleccionada = pantalla == pantallaActual
            NavigationBarItem(
                selected = seleccionada,
                onClick = { onPantallaSeleccionada(pantalla) },
                icon = {
                    Icon(
                        imageVector = pantalla.icono,
                        contentDescription = pantalla.titulo
                    )
                },
                label = { Text(pantalla.titulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VerdeOscuro,
                    selectedTextColor = VerdeOscuro,
                    indicatorColor = TurquesaAcento,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}