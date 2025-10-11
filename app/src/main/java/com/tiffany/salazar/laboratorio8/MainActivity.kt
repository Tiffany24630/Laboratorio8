package com.tiffany.salazar.laboratorio8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.tiffany.salazar.laboratorio8.ui.theme.Laboratorio8Theme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import com.tiffany.salazar.laboratorio8.ui.theme.Laboratorio8Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Laboratorio8Theme {
                // Suponiendo que tienes un Factory para inyectar el repositorio
                // La inicialización de 'repository' se hace arriba como placeholder
                val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
                HomeScreen(viewModel = homeViewModel)
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    // Usamos 'by' para delegar y obtener el valor directamente
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            HomeTopAppBar(onProfileClick = { /* Navegar a Perfil */ })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SearchBar(
                query = searchQuery,
                onQueryChanged = { viewModel.onQueryChanged(it) }
            )
            // Aquí irían las búsquedas recientes
            // RecentSearches( ... )

            PhotoGrid(photos = viewModel.photos)
        }
    }
}

// Preview para ver el diseño en el editor
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Laboratorio8Theme {
        // Para el preview, necesitamos un ViewModel falso
        val fakeViewModel = HomeViewModel(Any())
        HomeScreen(viewModel = fakeViewModel)
    }
}
