package com.tiffany.salazar.laboratorio8

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeViewModelFactory(private val repository: Any) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Primero, verifica si la clase es HomeViewModel
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            // Si lo es, crea una instancia y la devuelve
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        // Si no, lanza una excepción
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
