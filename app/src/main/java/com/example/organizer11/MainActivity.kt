package com.example.organizer11

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var btnThemeSwitch: ImageButton
    private lateinit var sharedPreferences: SharedPreferences

    // 1. Registro para el permiso de notificaciones (Solo Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Aquí recibimos la respuesta del usuario (Si o No).
        // Como ya no pedimos nada más después, no hace falta poner código extra aquí.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        loadThemePreference()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuración de Vistas y Navegación
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNav = findViewById(R.id.bottom_navigation_view)
        btnThemeSwitch = findViewById(R.id.btn_theme_switch)
        bottomNav.setupWithNavController(navController)
        setupThemeButton()
        setupVisibility()

        // 2. Pedir SOLO permiso de notificaciones al iniciar
        checkNotificationPermissions()
    }

    private fun checkNotificationPermissions() {
        // Solo es necesario en Android 13 (Tiramisu) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si no tiene permiso, lanza la ventanita estándar
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // --- FUNCIONES DE TEMA Y NAVEGACIÓN (Sin cambios) ---

    private fun loadThemePreference() {
        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        setAppTheme(isDarkMode)
    }

    private fun setupThemeButton() {
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        updateThemeIcon(isDarkMode)

        btnThemeSwitch.setOnClickListener {
            val currentMode = sharedPreferences.getBoolean("dark_mode", false)
            val newMode = !currentMode
            sharedPreferences.edit().putBoolean("dark_mode", newMode).apply()
            setAppTheme(newMode)
            updateThemeIcon(newMode)
        }
    }

    private fun setAppTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun updateThemeIcon(isDarkMode: Boolean) {
        if (isDarkMode) {
            btnThemeSwitch.setImageResource(R.drawable.ic_sun)
        } else {
            btnThemeSwitch.setImageResource(R.drawable.ic_moon)
        }
    }

    private fun setupVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainListFragment,
                R.id.starredListFragment -> {
                    showControls()
                }
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