package com.example.movies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.movies.model.Global

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnRegistro: Button = findViewById(R.id.btnRegistro)
        val btnIngresar: Button = findViewById(R.id.btnIngresar)
        val txtUser: EditText = findViewById(R.id.txtUsuario)
        val txtPassword: EditText = findViewById(R.id.txtContrasenia)

        fun sendMessage(message: String) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(intent)
        }

        btnIngresar.setOnClickListener() {
            val db = DBHelper(this, null)
            val username = txtUser.text.toString()
            val user = db.getUser(username)

            if (user != null && user.password.equals(txtPassword.text.toString())) {
                Global.loggedUserId = user.id
                val intent = Intent(this, MainActivity::class.java).apply {}
                startActivity(intent)
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_LONG).show()
            }
        }

        btnRegistro.setOnClickListener() {
            val intent = Intent(this, SignInActivity::class.java).apply {}
            startActivity(intent)
        }
    }


}