package com.example.login

import android.icu.text.NumberFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class PecaaquiFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    private lateinit var recyclerViewHamburguer: RecyclerView
    private lateinit var recyclerViewBebida: RecyclerView
    private lateinit var recyclerViewPorcao: RecyclerView
    private lateinit var adapterHamburguer: ConsumivelAdapter
    private lateinit var adapterBebida: ConsumivelAdapter
    private lateinit var adapterPorcao: ConsumivelAdapter
    val carrinho = mutableListOf<Consumivel>()
    var totalPreco: Double = 0.00
    lateinit var recyclerViewCarrinho: RecyclerView
    lateinit var adapterCarrinho: ConsumivelAdapter
    lateinit var tvTotalPreco: TextView

    // Mudei para listas separadas
    private val hamburgersList = mutableListOf<Consumivel>()
    private val bebidasList = mutableListOf<Consumivel>()
    private val porcoesList = mutableListOf<Consumivel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pecaaqui, container, false)

        // Inicializa o Firestore no onCreateView para garantir que ele seja inicializado antes de ser utilizado
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Referências aos componentes doa layout
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val tvHome: TextView = view.findViewById(R.id.tvHome)
        val tvPedidos: TextView = view.findViewById(R.id.tvPedidos)
        val tvPecaaqui: TextView = view.findViewById(R.id.tvPecaaqui)
        val ivLogo: ImageView = view.findViewById(R.id.ivLogo)
        val btnFinalizarCompra = view.findViewById<Button>(R.id.btn_finalizar_compra)
        btnFinalizarCompra.setOnClickListener {
            finalizarCompra()
        }
        // Referência ao botão de limpar carrinho
        val btnLimparCarrinho = view.findViewById<Button>(R.id.btn_limpar_carrinho)
        btnLimparCarrinho.setOnClickListener {
            limparCarrinho()  // Chama a função para limpar o carrinho
        }

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

        // Configuração dos RecyclerViews para exibir na vertical
        recyclerViewHamburguer = view.findViewById(R.id.rvHamburguer)
        recyclerViewBebida = view.findViewById(R.id.rvBebida)
        recyclerViewPorcao = view.findViewById(R.id.rvPorcao)

        recyclerViewHamburguer.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewBebida.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewPorcao.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // Carrinho
        recyclerViewCarrinho = view.findViewById(R.id.rvCarrinho)
        tvTotalPreco = view.findViewById(R.id.tvTotalPreco)

        recyclerViewCarrinho.layoutManager = LinearLayoutManager(context)
        adapterCarrinho = ConsumivelAdapter(carrinho, requireContext()) {}
        recyclerViewCarrinho.adapter = adapterCarrinho

        // Configuração dos adaptadores para consumíveis
        adapterHamburguer = ConsumivelAdapter(hamburgersList, requireContext()) { addToCart(it) }
        adapterBebida = ConsumivelAdapter(bebidasList, requireContext()) { addToCart(it) }
        adapterPorcao = ConsumivelAdapter(porcoesList, requireContext()) { addToCart(it) }

        recyclerViewHamburguer.adapter = adapterHamburguer
        recyclerViewBebida.adapter = adapterBebida
        recyclerViewPorcao.adapter = adapterPorcao

        // Ações para navegar entre os fragmentos
        ivLogo.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }
        tvHome.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }
        tvPedidos.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(PedidosFragment())
        }
        tvPecaaqui.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(PecaaquiFragment())
        }

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

        db.collection("consumiveis")
            .orderBy("nome")
            .get()
            .addOnSuccessListener { querySnapshot ->
                try {
                    // Limpa as listas antes de preencher
                    hamburgersList.clear()
                    bebidasList.clear()
                    porcoesList.clear()

                    // Adiciona os consumíveis do Firestore na lista
                    for (document in querySnapshot) {
                        val consumivel = document.toObject(Consumivel::class.java)
                        // Adiciona o ID do documento apenas em memória, não no Firestore
                        consumivel.id = document.id
                        when (consumivel.tipo.trim()) {
                            "Hambúrguer" -> hamburgersList.add(consumivel)
                            "Bebidas" -> bebidasList.add(consumivel)
                            "Porções" -> porcoesList.add(consumivel)
                        }
                    }

                    // Log das listas para verificar o conteúdo
                    Log.d("PecaaquiFragment", "Hamburgers List: $hamburgersList")
                    Log.d("PecaaquiFragment", "Bebidas List: $bebidasList")
                    Log.d("PecaaquiFragment", "Porcoes List: $porcoesList")

                    // Controla a visibilidade dos RecyclerViews com base na quantidade de itens
                    recyclerViewHamburguer.visibility = if (hamburgersList.isEmpty()) View.GONE else View.VISIBLE
                    recyclerViewBebida.visibility = if (bebidasList.isEmpty()) View.GONE else View.VISIBLE
                    recyclerViewPorcao.visibility = if (porcoesList.isEmpty()) View.GONE else View.VISIBLE

                    adapterHamburguer = ConsumivelAdapter(hamburgersList, requireContext()) { addToCart(it) }
                    adapterBebida = ConsumivelAdapter(bebidasList, requireContext()) { addToCart(it) }
                    adapterPorcao = ConsumivelAdapter(porcoesList, requireContext()) { addToCart(it) }

                    // Atribui os adaptadores aos RecyclerViews
                    recyclerViewHamburguer.adapter = adapterHamburguer
                    recyclerViewBebida.adapter = adapterBebida
                    recyclerViewPorcao.adapter = adapterPorcao

                } catch (e: Exception) {
                    Log.e("PecaaquiFragment", "Erro ao processar os dados: $e")
                    Toast.makeText(requireContext(), "Erro ao processar os dados: $e", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("PecaaquiFragment", "Erro ao buscar dados: $e")
                Toast.makeText(requireContext(), "Erro ao buscar dados: $e", Toast.LENGTH_SHORT).show()
            }

        return view
    }

    fun addToCart(consumivel: Consumivel) {
        carrinho.add(consumivel)
        totalPreco += consumivel.preco
        val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        tvTotalPreco.text = "Total: ${format.format(totalPreco)}"
        adapterCarrinho.notifyDataSetChanged()
    }

    fun finalizarCompra() {
        // Obtenha o UID do usuário logado
        val uid = getUserUID()
        if (uid.isNullOrEmpty()) {
            Toast.makeText(context, "Erro: usuário não está logado.", Toast.LENGTH_SHORT).show()
            (activity as? LoginActivity)?.replaceFragment(LoginFragment())
            return
        }
        // Obtenha a data e hora atual
        val dataHoraMillis = System.currentTimeMillis()

        // Formate a data para o formato desejado
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dataHoraFormatada = format.format(Date(dataHoraMillis))

        // Crie uma lista com as IDs dos itens no carrinho
        val itensIds = carrinho.mapNotNull { it.id }

        // Crie o objeto do pedido
        val pedido = hashMapOf(
            "uid" to uid,
            "data_hora" to dataHoraFormatada,
            "itens" to itensIds
        )

        // Salve o pedido na coleção 'pedidos' no Firestore
        db.collection("pedidos")
            .add(pedido)
            .addOnSuccessListener {
                Toast.makeText(context, "Finalização da compra!", Toast.LENGTH_SHORT).show()
                carrinho.clear()
                adapterCarrinho.notifyDataSetChanged()
                tvTotalPreco.text = "Preço Total: R$ 0.00"
            }
            .addOnFailureListener { e ->
                Log.e("PecaaquiFragment", "Erro ao finalizar compra", e)
                Toast.makeText(context, "Erro ao finalizar compra: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun limparCarrinho() {
        // Limpa a lista de itens do carrinho
        carrinho.clear()
        // Zera o preço total
        totalPreco = 0.00
        // Atualiza o texto do preço total na interface
        tvTotalPreco.text = "Total: R$ 0.00"
        // Notifica o adaptador para atualizar a exibição
        adapterCarrinho.notifyDataSetChanged()
        // Exibe uma mensagem de sucesso
        Toast.makeText(context, "Carrinho limpo!", Toast.LENGTH_SHORT).show()
    }


    fun getUserUID(): String? {
        val currentUser = auth.currentUser
        return currentUser?.uid
    }

    data class Consumivel(
        val descricao: String = "",
        val foto: String = "",
        val nome: String = "",
        val preco: Double = 0.00,
        val tipo: String = "",
        var id: String? = null // O campo id será temporário, apenas em memória
    )
}
