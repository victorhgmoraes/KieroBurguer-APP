package com.example.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class PedidosFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var tabelaPedidos: TableLayout
    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabelaPedidos = view.findViewById(R.id.tabela_pedidos)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Obtenha a instância do Firestore diretamente
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Carregar usuários do Firestore
        carregarPedidos()

        // Inflar o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_pedidos, container, false)

        // Encontrar os elementos da UI
        val btnLogin: Button = view.findViewById(R.id.btnLogin)
        val ivLogo: ImageView = view.findViewById(R.id.ivLogo)
        val tvMeusPedidos: TextView = view.findViewById(R.id.tv_meus_pedidos)
        val tvPecaaqui: TextView = view.findViewById(R.id.tvPecaaqui)

        fun updateUI() {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Define o botão como "Logout"
                btnLogin.text = "Logout"
            } else {
                btnLogin.text = "Login"
            }
        }

        // Verifica se o usuário está logado e atualiza a interface
        updateUI()

        // Ação do botão Login/Logout
        btnLogin.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Usuário está logado, realizar logout
                auth.signOut()
                btnLogin.text = "Login"
                (activity as? LoginActivity)?.replaceFragment(LoginFragment())
            } else {
                // Usuário não está logado, redireciona para o login
                (activity as? LoginActivity)?.replaceFragment(LoginFragment())
            }
        }

        tvPecaaqui.setOnClickListener {
            // Troca para o fragmento de pedidos
            (activity as? LoginActivity)?.replaceFragment(PecaaquiFragment())
        }

        // Ação do link "Logo"
        ivLogo.setOnClickListener {
            // Troca para o fragment de home
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }

        return view
    }

    private fun carregarPedidos() {
        val userId = auth.currentUser?.uid ?: return

        // Primeiro, recuperamos os pedidos do usuário logado
        db.collection("pedidos")
            .whereEqualTo("uid", userId) // Filtra os pedidos pelo usuário logado
            .get()
            .addOnSuccessListener { pedidosSnapshot ->
                for (pedidoDoc in pedidosSnapshot) {
                    val pedidoId = pedidoDoc.id
                    val dataHora = pedidoDoc.getString("data_hora") ?: ""
                    val itensIds = pedidoDoc.get("itens") as? List<String> ?: emptyList()

                    // Recuperar endereço do usuário
                    db.collection("tbl_login").document(userId).get()
                        .addOnSuccessListener { loginDoc ->
                            val endereco = formatarEndereco(loginDoc)
                            carregarItensPedido(itensIds) { itens, precoTotal ->
                                adicionarLinhaPedidos(dataHora, itens, precoTotal, endereco)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao carregar pedidos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarItensPedido(
        itensIds: List<String>,
        callback: (String, Double) -> Unit
    ) {
        var itensDetalhes = ""
        var precoTotal = 0.0
        val itensCount = itensIds.size
        var itensProcessed = 0

        for (itemId in itensIds) {
            db.collection("consumiveis").document(itemId).get()
                .addOnSuccessListener { itemDoc ->
                    val nome = itemDoc.getString("nome") ?: "Desconhecido"
                    val preco = itemDoc.getDouble("preco") ?: 0.0
                    itensDetalhes += "$nome - R$ %.2f\n".format(preco)
                    precoTotal += preco
                }
                .addOnCompleteListener {
                    itensProcessed++
                    if (itensProcessed == itensCount) {
                        callback(itensDetalhes.trim(), precoTotal)
                    }
                }
        }
    }

    private fun formatarEndereco(loginDoc: com.google.firebase.firestore.DocumentSnapshot): String {
        val logradouro = loginDoc.getString("Logradouro_Login") ?: "Desconhecido"
        val numero = loginDoc.getString("Numero_Login") ?: "S/N"
        val complemento = loginDoc.getString("Complemento_Login") ?: ""
        val bairro = loginDoc.getString("Bairro_Login") ?: ""
        val cidade = loginDoc.getString("Cidade_Login") ?: ""
        val estado = loginDoc.getString("Estado_Login") ?: ""

        return if (complemento.isNotEmpty()) {
            "$logradouro, $numero, $complemento\n$bairro, $cidade - $estado"
        } else {
            "$logradouro, $numero\n$bairro, $cidade - $estado"
        }
    }

    private fun adicionarLinhaPedidos(
        dataHora: String,
        itens: String,
        precoTotal: Double,
        endereco: String
    ) {
        val inflater = LayoutInflater.from(context)
        val linhaView = inflater.inflate(R.layout.item_pedido, tabelaPedidos, false) as TableRow

        linhaView.findViewById<TextView>(R.id.tvData).text = dataHora
        linhaView.findViewById<TextView>(R.id.tvNomePreco).text = itens
        linhaView.findViewById<TextView>(R.id.tvPrecoTotal).text = "R$ %.2f".format(precoTotal)
        linhaView.findViewById<TextView>(R.id.tvEndereço).text = endereco

        tabelaPedidos.addView(linhaView)
    }
}
