package com.example.login

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var isPasswordVisible = false
    private lateinit var auth: FirebaseAuth
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
        // Inicializa o FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Infla o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Referências aos componentes
        val etUsuario: EditText = view.findViewById(R.id.etUsuario)
        val etSenha: EditText = view.findViewById(R.id.etSenha)
        val tvMostrarSenha: TextView = view.findViewById(R.id.tvMostrarSenha)
        val btnEntrar: Button = view.findViewById(R.id.btnEntrar)
        val tvCadastro: TextView = view.findViewById(R.id.tvCadastro)
        val tvVoltar: TextView = view.findViewById(R.id.tvVoltar)

        // Mostrar ou ocultar senha ao clicar no TextView
        tvMostrarSenha.setOnClickListener {
            if (isPasswordVisible) {
                etSenha.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                tvMostrarSenha.text = "Mostrar Senha"
            } else {
                etSenha.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                tvMostrarSenha.text = "Ocultar Senha"
            }
            etSenha.setSelection(etSenha.text.length) // Mantém o cursor no final
            isPasswordVisible = !isPasswordVisible
        }

        // Ação do botão Entrar
        btnEntrar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            if (usuario.isNotEmpty() && senha.isNotEmpty()) {
                autenticarUsuario(usuario, senha)
            } else {
                Toast.makeText(context, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Ação do link "Cadastre-se"
        tvVoltar.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }

        // Ação do link "Cadastre-se"
        tvCadastro.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(CadastroFragment())
        }

        return view
    }

    // Função para autenticar o usuário usando nome de usuário e senha
    private fun autenticarUsuario(usuario: String, senha: String) {
        // Busque o usuário no Firestore com o nome de usuário fornecido
        val userRef = db.collection("tbl_login").whereEqualTo("User_Login", usuario)

        userRef.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val email = document.getString("Email_Login") ?: ""
                    val tipoUsuario = document.getLong("Tipo_Usuario")?.toInt()

                    if (email.isNotEmpty()) {
                        // Se o email for encontrado, autentique o usuário com o Firebase Authentication
                        auth.signInWithEmailAndPassword(email, senha)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Login bem-sucedido
                                    Toast.makeText(context, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show()

                                    // Verificar o tipo de usuário
                                    if (tipoUsuario == 1) {
                                        // Redireciona para GerenciamentoFragment se for administrador
                                        (activity as? LoginActivity)?.replaceFragment(GerenciamentoFragment())
                                    } else {
                                        // Redireciona para HomeFragment se for usuário comum
                                        (activity as? LoginActivity)?.replaceFragment(HomeFragment())
                                    }
                                } else {
                                    // Se o login falhar
                                    Toast.makeText(context, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Erro ao tentar autenticar", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Se não encontrar o usuário no Firestore
                    Toast.makeText(context, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Erro ao buscar dados do usuário: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}