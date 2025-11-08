package com.example.organizer11

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Encontrar el NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 2. Encontrar y conectar la Barra de Navegación
        bottomNav = findViewById(R.id.bottom_navigation_view)
        bottomNav.setupWithNavController(navController)

        // 3. Ocultar la barra inferior en las pantallas de "Añadir" y "Detalle"
        setupBottomNavVisibility()
    }

    private fun setupBottomNavVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // IDs de tu nav_graph.xml
                R.id.mainListFragment -> showBottomNav()
                // R.id.favoritesFragment -> showBottomNav() // <-- Descomenta si añades favoritos
                else -> hideBottomNav() // Oculta en "add" y "detail"
            }
        }
    }

    private fun showBottomNav() {
        bottomNav.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        bottomNav.visibility = View.GONE
    }
}