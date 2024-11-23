package com.example.fooddelivery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var telephoneEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)
        auth = Firebase.auth

        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        nameEditText = view.findViewById(R.id.nameEditText)
        telephoneEditText = view.findViewById(R.id.telephoneEditText)

        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val name = nameEditText.text.toString()
            val phone = telephoneEditText.text.toString()

            if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank() && phone.isNotBlank()) {
                if (isPhoneValid(phone)) {
                    registerUser(email, password) { success, error ->
                        if (success) {
                            registerUserInfo(email = email, name = name, phone = phone) { success, error ->
                                if (success) {
                                    Toast.makeText(context, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                                    Intent(activity, FoodMainActivity::class.java).apply {
                                        startActivity(this)
                                    }
                                } else {
                                    Toast.makeText(context, "Ошибка: $error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Ошибка: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Неверный формат телефона", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Поля не введены", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.BackButton).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    // Функция проверки номера телефона
    private fun isPhoneValid(phone: String): Boolean {
        val phonePattern = "^\\+7\\d{10}$"
        return phone.matches(Regex(phonePattern))
    }

    private fun registerUser(
        email: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    private fun registerUserInfo(
        email: String,
        name: String,
        phone: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val db = Firebase.firestore

        db.collection("Users").add(
            UserData(
                name = name,
                email = email,
                phone = phone,
                address = null,
                role = "Client"
            )
        ).addOnSuccessListener { documentReference ->
            Log.i("Firestore", "Документ добавлен с ID: ${documentReference.id}")
            onComplete(true, null)
        }
            .addOnFailureListener { e ->
                Log.e("Firestore", e.toString())
                onComplete(false, e.message)
            }
    }
}
