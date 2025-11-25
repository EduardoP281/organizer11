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
import com.example.organizer11.databinding.FragmentSingUpBinding // <-- CAMBIO 3: 'SingUp' (con G)
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth


class SingUpFragment : Fragment() { // <-- CAMBIO 4: 'SingUp' (con G)

    private lateinit var auth:FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSingUpBinding // <-- CAMBIO 3: 'SingUp' (con G)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =FragmentSingUpBinding.inflate(inflater,container,false) // <-- CAMBIO 3
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun registerEvents() {
        // Este ID 'authTextView' debe estar en tu XML de 'sing_up'
        binding.authTextView.setOnClickListener {
            // CAMBIO 5: Acción a 'SingIn' (con G)
            navControl.navigate(R.id.action_singUpFragment_to_singInFragment)
        }

        // Estos IDs deben estar en tu XML
        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()
            val veryPass = binding.verifyPassEt.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && veryPass.isNotEmpty()) {
                if (pass == veryPass) {
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(requireActivity()) { task -> // Usamos requireActivity()
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                // CAMBIO 6: Acción a tu lista principal
                                navControl.navigate(R.id.action_singUpFragment_to_mainListFragment)
                            } else {
                                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init(view: View) {
        navControl =Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
    }
}