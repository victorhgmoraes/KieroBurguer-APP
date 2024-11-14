package com.example.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class PecaaquiFragment : Fragment() {

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
        // Infla o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_pecaaqui, container, false)

        // Referências aos componentes do layout
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val tvHome: TextView = view.findViewById(R.id.tvHome)
        val tvPedidos: TextView = view.findViewById(R.id.tvPedidos)
        val tvPecaaqui: TextView = view.findViewById(R.id.tvPecaaqui)
        val ivLogo: ImageView = view.findViewById(R.id.ivLogo)
        val btnAddToCart1 = view.findViewById<Button>(R.id.btnAddToCart)
        val btnAddToCart2 = view.findViewById<Button>(R.id.btnAddToCart2)

        val tvHamburguerName1 = view.findViewById<TextView>(R.id.tvHamburguerName)
        val tvHamburguerPrice1 = view.findViewById<TextView>(R.id.tvHamburguerPrice)

        val tvHamburguerName2 = view.findViewById<TextView>(R.id.tvHamburguerName2)
        val tvHamburguerPrice2 = view.findViewById<TextView>(R.id.tvHamburguerPrice2)

        // Configura os botões com onClickListener
        ivLogo.setOnClickListener {
            // Navega para a própria Home (recarrega o fragmento)
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }

        // Configura os botões com onClickListener
        tvHome.setOnClickListener {
            // Navega para a própria Home (recarrega o fragmento)
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }

        tvPedidos.setOnClickListener {
            // Navega para o fragmento de pedidos
            (activity as? LoginActivity)?.replaceFragment(PedidosFragment())
        }

        tvPecaaqui.setOnClickListener {
            // Navega para o fragmento Peça Aqui
            (activity as? LoginActivity)?.replaceFragment(PecaaquiFragment())
        }

        btnLogin.setOnClickListener {
            // Navega para o fragmento de login
            (activity as? LoginActivity)?.replaceFragment(LoginFragment())
        }

        // Eventos de clique
        btnLogin.setOnClickListener {
            // Ação para login, pode ser uma navegação ou outro comportamento
            Toast.makeText(requireContext(), "Login clicado!", Toast.LENGTH_SHORT).show()
        }

        btnAddToCart1.setOnClickListener {
            // Adiciona o primeiro hambúrguer ao carrinho
            Toast.makeText(requireContext(), "${tvHamburguerName1.text} adicionado ao carrinho", Toast.LENGTH_SHORT).show()
        }

        btnAddToCart2.setOnClickListener {
            // Adiciona o segundo hambúrguer ao carrinho
            Toast.makeText(requireContext(), "${tvHamburguerName2.text} adicionado ao carrinho", Toast.LENGTH_SHORT).show()
        }

        // Ação do link "Login"
        btnLogin.setOnClickListener {
            // Troca para o fragment de login
            (activity as? LoginActivity)?.replaceFragment(LoginFragment())
        }

        return view
    }
}