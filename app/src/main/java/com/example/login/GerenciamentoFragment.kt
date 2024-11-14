package com.example.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class GerenciamentoFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtenha a instância do Firestore diretamente
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_gerenciamento, container, false)

        // Acessar os botões usando findViewById
        val btnCardapio = view.findViewById<Button>(R.id.btnCardápio)
        val btnUsuarios = view.findViewById<Button>(R.id.btnUsuarios)

        // Ação do link "Cadastre-se"
        btnCardapio.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(CardapioadmFragment())
        }

        // Ação do link "Cadastre-se"
        btnUsuarios.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(UsuariosadmFragment())
        }

        return view
    }
}
