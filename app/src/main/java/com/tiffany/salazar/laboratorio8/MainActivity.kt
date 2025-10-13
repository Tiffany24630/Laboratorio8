package com.tiffany.salazar.laboratorio8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import com.tiffany.salazar.laboratorio8.di.ServiceLocator
import com.tiffany.salazar.laboratorio8.ui.DetailsScreen
import com.tiffany.salazar.laboratorio8.ui.PhotoGrid
import com.tiffany.salazar.laboratorio8.viewmodel.DetailsViewModel
import com.tiffany.salazar.laboratorio8.viewmodel.DetailsViewModelFactory
import com.tiffany.salazar.laboratorio8.viewmodel.HomeViewModel
import com.tiffany.salazar.laboratorio8.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inyección de dependencias manual
        val repository = ServiceLocator.provideRepository(this)
        val homeFactory = HomeViewModelFactory(repository)
        val detailsFactory = DetailsViewModelFactory(repository)

        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            val vm = ViewModelProvider(this@MainActivity, homeFactory)[HomeViewModel::class.java]
                            HomeScreenHost(vm = vm, onPhotoClick = { id -> navController.navigate("details/$id") })
                        }
                        composable(
                            route = "details/{photoId}",
                            arguments = listOf(navArgument("photoId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("photoId") ?: ""
                            val vm = ViewModelProvider(this@MainActivity, detailsFactory)[DetailsViewModel::class.java]
                            // Cargar datos solo si no se han cargado ya para este ID
                            LaunchedEffect(id) {
                                vm.loadById(id)
                            }
                            DetailsScreen(viewModel = vm)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenHost(vm: HomeViewModel, onPhotoClick: (String) -> Unit) {
    val query by vm.query.collectAsState()
    val items = vm.photos.collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fotos") },
                actions = {
                    IconButton(onClick = { /* Navegar a perfil */ }) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Perfil")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TextField(
                value = query,
                onValueChange = { vm.setQuery(it) },
                label = { Text("Buscar por autor") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
            PhotoGrid(
                items = items,
                onPhotoClick = onPhotoClick,
                onToggleFavorite = { photo ->
                    coroutineScope.launch {
                        vm.toggleFavorite(photo)
                        items.refresh() // Refresca la lista para mostrar el cambio de favorito
                    }
                }
            )
        }
    }
}
