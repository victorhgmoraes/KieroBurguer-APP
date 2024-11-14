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

class HomeFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var tvHome: TextView
    private lateinit var tvPedidos: TextView
    private lateinit var tvPecaaqui: TextView
    private lateinit var btnLoginLogout: Button
    private lateinit var tvWelcome: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inicializa o FirebaseAuth e Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Infla o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_homepage, container, false)

        // Inicializa os componentes da interface
        tvHome = view.findViewById(R.id.textView)
        tvPedidos = view.findViewById(R.id.textView3)
        tvPecaaqui = view.findViewById(R.id.textView2)
        tvWelcome = view.findViewById(R.id.tvWelcome)
        btnLoginLogout = view.findViewById(R.id.btnLoginLogout)

        // Verifica se o usuário está logado e atualiza a interface
        updateUI()

        // Ação do botão Login/Logout
        btnLoginLogout.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Usuário está logado, realizar logout
                auth.signOut()
                btnLoginLogout.text = "Login"
                tvWelcome.text = "Olá, seja bem-vindo!" // Mensagem padrão
                (activity as? LoginActivity)?.replaceFragment(LoginFragment())
            } else {
                // Usuário não está logado, redireciona para o login
                (activity as? LoginActivity)?.replaceFragment(LoginFragment())
            }
        }

        // Configura os botões de navegação com onClickListener
        tvHome.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }

        tvPedidos.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(PedidosFragment())
        }

        tvPecaaqui.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(PecaaquiFragment())
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
            btnLoginLogout.text = "Logout"
        } else {
            // Caso não esteja logado, exibe a mensagem padrão
            tvWelcome.visibility = View.GONE
            btnLoginLogout.text = "Login"
        }
    }
}
