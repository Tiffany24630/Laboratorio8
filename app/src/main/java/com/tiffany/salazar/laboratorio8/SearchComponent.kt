package com.tiffany.salazar.laboratorio8

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier // Añadido para flexibilidad
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        placeholder = { Text("Buscar fotos...") },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true // Buena práctica para barras de búsqueda
    )
}
