package com.example.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Mostra o fragment de login ao iniciar a activity
        if (savedInstanceState == null) {
            replaceFragment(LoginFragment())
        }
    }

    // Função para substituir o fragment atual
    public fun replaceFragment(fragment: Fragment) { // ou internal
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
