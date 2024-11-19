package com.example.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class GerenciamentoFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var tvWelcome: TextView
    private lateinit var btnCardapio: Button
    private lateinit var btnUsuarios: Button
    private lateinit var btnLogout: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtenha a instância do Firestore diretamente
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inicializa o FirebaseAuth e Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        // Inflar o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_gerenciamento, container, false)

        // Acessar os botões usando findViewById
        btnCardapio = view.findViewById(R.id.btnCardápio)
        btnUsuarios = view.findViewById(R.id.btnUsuarios)
        btnLogout = view.findViewById(R.id.btnLogout)
        tvWelcome = view.findViewById(R.id.tvWelcome)

        //atualiza a interface
        updateUI()

        // Ação do botão Login/Logout
        btnLogout.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Usuário está logado, realizar logout
                auth.signOut()
                btnLogout.text = "Login"
                tvWelcome.text = "Olá, seja bem-vindo!" // Mensagem padrão
                (activity as? LoginActivity)?.replaceFragment(LoginFragment())
            } else {
                // Usuário não está logado, redireciona para o login
                (activity as? LoginActivity)?.replaceFragment(LoginFragment())
            }
        }

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

    private fun updateUI() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Busca o nome de usuário no Firestore e exibe na mensagem de boas-vindas
            tvWelcome.visibility = View.VISIBLE
            db.collection("tbl_login")
                .whereEqualTo("Email_Login", currentUser.email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val userLogin = documents.first().getString("User_Login") ?: ""
                        tvWelcome.text = "Olá $userLogin, seja bem-vindo de volta!"
                    } else {
                        tvWelcome.text = "Olá ${currentUser.email}, seja bem-vindo de volta!"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao buscar dados do usuário", Toast.LENGTH_SHORT).show()
                }

            // Define o botão como "Logout"
            btnLogout.text = "Logout"
        } else {
            // Caso não esteja logado, exibe a mensagem padrão
            tvWelcome.visibility = View.GONE
            btnLogout.text = "Login"
        }
    }
}

