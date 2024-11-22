package com.example.login

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale

class CadastroFragment : Fragment() {

    private lateinit var etUsuario: EditText
    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var etConfirmarSenha: EditText
    private lateinit var etCPF: EditText
    private lateinit var etCEP: EditText
    private lateinit var etLogradouro: EditText
    private lateinit var etNumero: EditText
    private lateinit var etComplemento: EditText
    private lateinit var etBairro: EditText
    private lateinit var etCidade: EditText
    private lateinit var etEstado: EditText
    private lateinit var btnCadastrar: Button
    private lateinit var btnLogin: Button
    private lateinit var tvTituloCadastro: TextView
    private lateinit var tvPedidos: TextView
    private lateinit var tvPecaaqui: TextView
    private lateinit var tvHome: TextView
    private lateinit var btnGerenciamento: TextView
    private lateinit var ivLogo: ImageView

    private lateinit var db: FirebaseFirestore

    private fun buscarDadosDoCEP(cep: String) {
        // Remove qualquer formatação no CEP
        val cepLimpo = cep.replace("-", "").trim()

        if (cepLimpo.length != 8) {
            Toast.makeText(context, "CEP inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "https://viacep.com.br/ws/$cepLimpo/json/"

        // Criação da thread para não bloquear a UI
        Thread {
            try {
                // Fazendo a requisição HTTP
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                // Verificando a resposta
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() // Acesso ao corpo da resposta

                    // Parseando o JSON
                    val json = JSONObject(responseBody ?: "{}")

                    // Extraindo os dados
                    val bairro = json.optString("bairro", "Não encontrado")
                    val cidade = json.optString("localidade", "Não encontrado")
                    val estado = json.optString("uf", "Não encontrado")
                    val logradouro = json.optString("logradouro", "Não encontrado")

                    // Atualizando a UI (precisa rodar na thread principal)
                    activity?.runOnUiThread {
                        etBairro.setText(bairro)
                        etCidade.setText(cidade)
                        etEstado.setText(estado)
                        etLogradouro.setText(logradouro)
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Erro ao buscar dados do CEP", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Erro ao realizar a requisição", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun formatarCPF(cpf: String): String {
        return cpf.replace(Regex("[^\\d]"), "").let {
            when {
                it.length > 9 -> "${it.substring(0, 3)}.${it.substring(3, 6)}.${it.substring(6, 9)}-${it.substring(9)}"
                it.length > 6 -> "${it.substring(0, 3)}.${it.substring(3, 6)}.${it.substring(6)}"
                it.length > 3 -> "${it.substring(0, 3)}.${it.substring(3)}"
                else -> it
            }
        }
    }

    private fun formatarCEP(cep: String): String {
        return cep.replace(Regex("[^\\d]"), "").let {
            if (it.length > 5) "${it.substring(0, 5)}-${it.substring(5)}" else it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtenha a instância do Firestore diretamente
        db = FirebaseFirestore.getInstance()

        etCPF.addTextChangedListener(object : TextWatcher {
            var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                isUpdating = true
                etCPF.setText(formatarCPF(s.toString()))
                etCPF.setSelection(etCPF.text.length)
                isUpdating = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        etCEP.addTextChangedListener(object : TextWatcher {
            var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                isUpdating = true
                etCEP.setText(formatarCEP(s.toString()))
                etCEP.setSelection(etCEP.text.length)
                isUpdating = false

                // Quando o CEP tem 8 dígitos, buscar os dados
                if (etCEP.text.length == 9) {
                    buscarDadosDoCEP(etCEP.text.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Limita o campo de CPF a 14 caracteres (formato: 000.000.000-00)
        etCPF.filters = arrayOf(InputFilter.LengthFilter(14))

        // Limita o campo de CEP a 9 caracteres (formato: 00000-000)
            etCEP.filters = arrayOf(InputFilter.LengthFilter(9))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_cadastro, container, false)

        // Inicializa os componentes da interface
        etUsuario = view.findViewById(R.id.etUsuarioCadastro)
        etNome = view.findViewById(R.id.etNomeCadastro)
        etEmail = view.findViewById(R.id.etEmailCadastro)
        etSenha = view.findViewById(R.id.etSenhaCadastro)
        etConfirmarSenha = view.findViewById(R.id.etConfirmarSenhaCadastro)
        etCPF = view.findViewById(R.id.etCPF)
        etCEP = view.findViewById(R.id.etCEP)
        etLogradouro = view.findViewById(R.id.etLogradouro)
        etNumero = view.findViewById(R.id.etNumero)
        etComplemento = view.findViewById(R.id.etComplemento)
        etBairro = view.findViewById(R.id.etBairro)
        etCidade = view.findViewById(R.id.etCidade)
        etEstado = view.findViewById(R.id.etEstado)
        btnCadastrar = view.findViewById(R.id.btnCadastrar)
        btnLogin = view.findViewById(R.id.btnLogin)
        tvTituloCadastro = view.findViewById(R.id.tvTituloCadastro)
        tvPedidos = view.findViewById(R.id.tvPedidos)
        tvPecaaqui = view.findViewById(R.id.tvPecaaqui)
        tvHome = view.findViewById(R.id.tvHome)
        ivLogo = view.findViewById(R.id.ivLogo)

        // Configura o botão de cadastro
        btnCadastrar.setOnClickListener {
            cadastrarUsuario()
        }

        // Ação do link "Logo"
        ivLogo.setOnClickListener {
            // Troca para o fragment de home
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }

        // Ação do link "Home"
        tvHome.setOnClickListener {
            // Troca para o fragment de home
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }

        // Ação do link "Cadastre-se"
        btnLogin.setOnClickListener {
            // Troca para o fragment de login
            (activity as? LoginActivity)?.replaceFragment(LoginFragment())
        }

        // Ação do link "Pedidos"
        tvPecaaqui.setOnClickListener {
            // Troca para o fragmento de pedidos
            (activity as? LoginActivity)?.replaceFragment(PecaaquiFragment())
        }

        tvPedidos.setOnClickListener {
            // Troca para o fragmento de pedidos
            (activity as? LoginActivity)?.replaceFragment(PedidosFragment())
        }



        return view
    }

    private fun cadastrarUsuario() {
        // Obtém os dados dos campos de entrada
        val usuario = etUsuario.text.toString().lowercase(Locale.getDefault())
        val nome = etNome.text.toString()
        val email = etEmail.text.toString()
        val senha = etSenha.text.toString()
        val confirmarSenha = etConfirmarSenha.text.toString()
        val cpf = etCPF.text.toString()
        val cep = etCEP.text.toString()
        val logradouro = etLogradouro.text.toString()
        val numero = etNumero.text.toString()
        val complemento = etComplemento.text.toString()
        val bairro = etBairro.text.toString()
        val cidade = etCidade.text.toString()
        val estado = etEstado.text.toString()

        // Verifica se todos os campos estão preenchidos, mas não obriga o "complemento"
        if (usuario.isEmpty() || nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty() ||
            cpf.isEmpty() || cep.isEmpty() || logradouro.isEmpty() || numero.isEmpty() || bairro.isEmpty() ||
            cidade.isEmpty() || estado.isEmpty()) {
            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
            return
        }

        // Verifica se as senhas coincidem
        if (senha != confirmarSenha) {
            Toast.makeText(context, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
            return
        }

    // Criando o usuário no Firebase Authentication
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
        .addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Usuário criado com sucesso no Firebase Authentication
                val user = FirebaseAuth.getInstance().currentUser

                // Dados para o Firebase
                val usuarioData = hashMapOf(
                    "User_Login" to usuario,
                    "Nome_Login" to nome,
                    "Email_Login" to email,
                    "CPF_Login" to cpf,
                    "CEP_Login" to cep,
                    "Logradouro_Login" to logradouro,
                    "Numero_Login" to numero,
                    "Complemento_Login" to complemento,
                    "Bairro_Login" to bairro,
                    "Cidade_Login" to cidade,
                    "Estado_Login" to estado,
                    "Tipo_Usuario" to 0 // Tipo de usuário padrão (0 pode ser para usuário comum)
                )

                // Usando o UID do Firebase Authentication para associar os dados
                if (user != null) {
                    db.collection("tbl_login")
                        .document(user.uid) // Usando o UID como o identificador do documento
                        .set(usuarioData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                            limparCampos()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Erro ao salvar dados no Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                // Se falhar na criação do usuário no Firebase Authentication
                Toast.makeText(context, "Erro ao cadastrar usuário: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun limparCampos() {
        etUsuario.text.clear()
        etNome.text.clear()
        etEmail.text.clear()
        etSenha.text.clear()
        etConfirmarSenha.text.clear()
        etCPF.text.clear()
        etCEP.text.clear()
        etLogradouro.text.clear()
        etNumero.text.clear()
        etComplemento.text.clear()
        etBairro.text.clear()
        etCidade.text.clear()
        etEstado.text.clear()
    }
}
