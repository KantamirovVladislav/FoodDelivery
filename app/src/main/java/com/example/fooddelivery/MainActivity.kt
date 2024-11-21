package com.example.fooddelivery

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)




        findViewById<Button>(R.id.registerButton).setOnClickListener {
            var email: String = emailEditText.text.toString()
            var password: String = passwordEditText.text.toString()
            if (email.isNotBlank() && password.isNotBlank()) {
                registerUser(email, password) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Ошибка: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Поля не введены", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            var email: String = emailEditText.text.toString()
            var password: String = passwordEditText.text.toString()
            if (email.isNotBlank() && password.isNotBlank()) {
                loginUser(email, password) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Вход успешен", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Ошибка: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Поля не введены", Toast.LENGTH_SHORT).show()
            }
        }
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