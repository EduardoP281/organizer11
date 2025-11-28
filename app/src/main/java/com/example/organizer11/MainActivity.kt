package com.example.organizer11

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView

    // Variables nuevas para el tema
    private lateinit var btnThemeSwitch: ImageButton
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Cargar el tema guardado ANTES de crear la vista
        // (Esto evita el "parpadeo" blanco al abrir la app si está en modo oscuro)
        loadThemePreference()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Encontrar el NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Encontrar vistas
        bottomNav = findViewById(R.id.bottom_navigation_view)
        btnThemeSwitch = findViewById(R.id.btn_theme_switch) // <-- Nuevo botón

        // Conectar la Barra de Navegación
        bottomNav.setupWithNavController(navController)

        // 2. Configurar la lógica del botón de tema
        setupThemeButton()

        // 3. Controlar visibilidad (Barra inferior y Botón de tema)
        setupVisibility()
    }

    private fun loadThemePreference() {
        // Leemos la memoria del teléfono para ver qué eligió el usuario antes
        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        setAppTheme(isDarkMode)
    }

    private fun setupThemeButton() {
        // Configurar el ícono inicial correcto (Sol o Luna)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        updateThemeIcon(isDarkMode)

        btnThemeSwitch.setOnClickListener {
            // Leer estado actual e invertirlo
            val currentMode = sharedPreferences.getBoolean("dark_mode", false)
            val newMode = !currentMode

            // Guardar la nueva elección
            sharedPreferences.edit().putBoolean("dark_mode", newMode).apply()

            // Aplicar el cambio visual
            setAppTheme(newMode)
            updateThemeIcon(newMode)
        }
    }

    private fun setAppTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            // Activa el modo oscuro de Android
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // Activa el modo claro
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun updateThemeIcon(isDarkMode: Boolean) {
        if (isDarkMode) {
            // Si es oscuro, mostramos el SOL para cambiar a claro
            btnThemeSwitch.setImageResource(R.drawable.ic_sun)
        } else {
            // Si es claro, mostramos la LUNA para cambiar a oscuro
            btnThemeSwitch.setImageResource(R.drawable.ic_moon)
        }
    }

    // Esta función reemplaza a tu antigua 'setupBottomNavVisibility'
    private fun setupVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // PANTALLAS DONDE SÍ QUEREMOS VER LA BARRA Y EL BOTÓN
                R.id.mainListFragment,
                R.id.starredListFragment -> {
                    showControls()
                }

                // EN TODAS LAS DEMÁS (Splash, Login, Añadir, Detalle) SE OCULTAN
                else -> {
                    hideControls()
                }
            }
        }
    }

    private fun showControls() {
        bottomNav.visibility = View.VISIBLE
        btnThemeSwitch.visibility = View.VISIBLE
    }

    private fun hideControls() {
        bottomNav.visibility = View.GONE
        btnThemeSwitch.visibility = View.GONE
    }
}