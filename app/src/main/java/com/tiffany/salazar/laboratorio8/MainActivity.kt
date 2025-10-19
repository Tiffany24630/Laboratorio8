package com.tiffany.salazar.laboratorio8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tiffany.salazar.laboratorio8.di.ServiceLocator
import com.tiffany.salazar.laboratorio8.ui.DetailsScreen
import com.tiffany.salazar.laboratorio8.ui.HomeScreen
import com.tiffany.salazar.laboratorio8.ui.theme.Laboratorio8Theme
import com.tiffany.salazar.laboratorio8.viewmodel.DetailsViewModel
import com.tiffany.salazar.laboratorio8.viewmodel.HomeViewModel
import com.tiffany.salazar.laboratorio8.viewmodel.HomeViewModelFactory
import com.tiffany.salazar.laboratorio8.viewmodel.DetailsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Laboratorio8Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = remember { ServiceLocator.provideRepository(context) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(repository)
            )
            HomeScreen(
                viewModel = homeViewModel,
                onPhotoClick = { photoId ->
                    navController.navigate("details/$photoId")
                },
                onProfileClick = {

                }
            )
        }
        composable(
            route = "details/{photoId}",
            arguments = listOf(navArgument("photoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getString("photoId") ?: ""
            val detailsViewModel: DetailsViewModel = viewModel(
                factory = DetailsViewModelFactory(repository, photoId)
            )
            DetailsScreen(
                viewModel = detailsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}