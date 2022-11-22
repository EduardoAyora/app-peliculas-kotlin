package com.example.movies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val txtUsuario: EditText = findViewById(R.id.txtUsuarioRegistro)
        val txtContra: EditText = findViewById(R.id.txtContraseniaRegistro)
        val btnRegistrarse: Button = findViewById(R.id.btnRegistrarse)

        btnRegistrarse.setOnClickListener() {
            val db = DBHelper(this, null)

            val username = txtUsuario.text.toString()
            val password = txtContra.text.toString()

            if (username.isEmpty()) {
                Toast.makeText(this,"El nombre de usuario es requerido", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val user = db.getUser(username)
            if (user != null) {
                Toast.makeText(this,"El usuario ya existe", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            db.addUser(username, password)

            Toast.makeText(this, "Se ha registrado correctamente", Toast.LENGTH_LONG).show()

            val intent = Intent(this, LoginActivity::class.java).apply {}
            startActivity(intent)
        }
    }
}