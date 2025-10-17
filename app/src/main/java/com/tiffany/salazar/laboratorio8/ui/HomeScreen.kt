package com.tiffany.salazar.laboratorio8.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.tiffany.salazar.laboratorio8.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onPhotoClick: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    val query by viewModel.query.collectAsState()
    val photos = viewModel.photos.collectAsLazyPagingItems()
    val recentQueries by viewModel.recentQueries.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showRecentQueries by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setQuery(it) },
                placeholder = { Text("Buscar fotos...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                trailingIcon = {
                    if (recentQueries.isNotEmpty()) {
                        IconButton(onClick = { showRecentQueries = !showRecentQueries }) {
                            Icon(Icons.Default.History, contentDescription = "Historial")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Lista de búsquedas recientes
            if (showRecentQueries && recentQueries.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(recentQueries) { recentQuery ->
                            Text(
                                text = recentQuery,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectRecentQuery(recentQuery)
                                        showRecentQueries = false
                                    }
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Divider()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Grid de fotos
            if (photos.itemCount > 0) {
                PhotoGrid(
                    items = photos,
                    onPhotoClick = onPhotoClick,
                    onToggleFavorite = { photo ->
                        // El toggle se maneja en el ViewModel
                    }
                )
            } else if (query.isNotEmpty() && !isLoading) {
                // Estado vacío
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron fotos para \"$query\"",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}