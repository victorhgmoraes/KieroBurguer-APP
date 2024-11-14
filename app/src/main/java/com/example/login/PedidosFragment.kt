package com.example.login

import android.annotation.SuppressLint
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
import com.google.firebase.firestore.FirebaseFirestore

class PedidosFragment : Fragment() {

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
        // Inflar o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_pedidos, container, false)

        // Encontrar os elementos da UI
        val btnLogin: Button = view.findViewById(R.id.btnLogin)
        val ivLogo: ImageView = view.findViewById(R.id.ivLogo)
        val tvMeusPedidos: TextView = view.findViewById(R.id.tv_meus_pedidos)
        val tableLayout: TableLayout = view.findViewById(R.id.tableLayout)
        val tvPecaaqui: TextView = view.findViewById(R.id.tvPecaaqui)

        // Ação do link "Cadastre-se"
        btnLogin.setOnClickListener {
            // Troca para o fragment de cadastro
            (activity as? LoginActivity)?.replaceFragment(LoginFragment())
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

    private fun adicionarPedidoNaTabela(
        tableLayout: TableLayout, pedido: String, dataHora: String,
        itens: String, total: String, endereco: String
    ) {
        val novaLinha = TableRow(context)

        // Criar TextViews para cada coluna da nova linha
        val tvPedido = TextView(context).apply {
            text = pedido
            setPadding(5, 5, 5, 5)
        }
        val tvDataHora = TextView(context).apply {
            text = dataHora
            setPadding(5, 5, 5, 5)
        }
        val tvItens = TextView(context).apply {
            text = itens
            setPadding(5, 5, 5, 5)
        }
        val tvTotal = TextView(context).apply {
            text = total
            setPadding(5, 5, 5, 5)
        }
        val tvEndereco = TextView(context).apply {
            text = endereco
            setPadding(5, 5, 5, 5)
        }

        // Adicionar os TextViews à nova linha
        novaLinha.addView(tvPedido)
        novaLinha.addView(tvDataHora)
        novaLinha.addView(tvItens)
        novaLinha.addView(tvTotal)
        novaLinha.addView(tvEndereco)

        // Adicionar a nova linha ao TableLayout
        tableLayout.addView(novaLinha)
    }
}
