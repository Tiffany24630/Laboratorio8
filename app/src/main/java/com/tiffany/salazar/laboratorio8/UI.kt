package com.tiffany.salazar.laboratorio8

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow

// Definición básica para que el código compile. Debería estar en su propio archivo.
data class Photo(val id: String, val url: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(onProfileClick: () -> Unit) {
    TopAppBar(
        title = { Text("Fotos") },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil"
                )
            }
        }
    )
}

@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        placeholder = { Text("Buscar fotos...") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun PhotoGrid(photos: Flow<PagingData<Photo>>) {
    // Convierte el Flow de PagingData en un LazyPagingItems que Compose puede observar
    val lazyPhotoItems = photos.collectAsLazyPagingItems()

    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        // El contenido se llenará cuando conectes el PagingSource real
        // Ejemplo de cómo se usaría:
        // items(lazyPhotoItems.itemCount) { index ->
        //     val photo = lazyPhotoItems[index]
        //     if (photo != null) {
        //         // Tu Composable de tarjeta de foto aquí
        //     }
        // }
    }
}
