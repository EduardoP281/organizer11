package com.example.organizer11.ui.session

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.organizer11.R
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)

        Handler(Looper.myLooper()!!).postDelayed({

            // ▼▼▼ ¡ESTE ES EL CAMBIO! ▼▼▼

            // 1. Primero, verificamos si hay un usuario logueado.
            //    Si lo hay, cerramos su sesión.
            if (auth.currentUser != null) {
                auth.signOut()
            }

            // 2. Ahora que estamos seguros de que nadie está logueado,
            //    SIEMPRE navegamos a la pantalla de inicio de sesión.
            navController.navigate(R.id.action_splashFragment_to_singInFragment)

        }, 2000)
    }
}