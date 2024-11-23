package com.example.fooddelivery

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        auth = Firebase.auth

        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)

        view.findViewById<Button>(R.id.LoginButton).setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isNotBlank() && password.isNotBlank()) {
                loginUser(email, password) { success, error ->
                    if (success) {
                        Toast.makeText(context, "Вход успешен", Toast.LENGTH_SHORT).show()
                        Intent(activity,FoodMainActivity::class.java).apply {
                            startActivity(this)
                        }
                    } else {
                        Toast.makeText(context, "Ошибка: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Поля не введены", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, RegisterFragment()).addToBackStack(null)
            }
        }

        return view
    }

    private fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }
}