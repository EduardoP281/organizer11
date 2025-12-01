package com.example.organizer11.ui.session // <-- CAMBIO 1: Tu paquete

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.organizer11.R // <-- CAMBIO 2: Tu archivo R
import com.example.organizer11.databinding.FragmentSingInBinding // <-- CAMBIO 3: 'SingIn' (con G)
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

// CAMBIO 4: Nombre de la clase 'SingIn' (con G)
class SingInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSingInBinding // <-- CAMBIO 3: 'SingIn' (con G)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSingInBinding.inflate(inflater,container,false) // <-- CAMBIO 3
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun registerEvents() {
        binding.textViewSignUp.setOnClickListener {
            // CAMBIO 5: Acción a 'SingUp' (con G)
            navControl.navigate(R.id.action_singInFragment_to_singUpFragment)
        }

        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()

            if(email.isNotEmpty()&& pass.isNotEmpty()){

                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(
                    OnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context, "Acceso exitoso", Toast.LENGTH_SHORT).show()
                            // CAMBIO 6: Acción a tu lista principal
                            navControl.navigate(R.id.action_singInFragment_to_mainListFragment)
                        }else{
                            Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }

    private fun init(view: View) {
        navControl = Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
    }
}