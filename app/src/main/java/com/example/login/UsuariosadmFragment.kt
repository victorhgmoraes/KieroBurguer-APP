package com.example.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class UsuariosadmFragment : Fragment() {

    private lateinit var voltarInicioTextView: TextView
    private lateinit var tituloGerenciamentoTextView: TextView
    private lateinit var tipoUsuario1Spinner: Spinner
    private lateinit var tipoUsuario2Spinner: Spinner
    private lateinit var salvarButton1: Button
    private lateinit var salvarButton2: Button
    private lateinit var excluirButton1: Button
    private lateinit var excluirButton2: Button

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
        // Infla o layout
        val view = inflater.inflate(R.layout.fragment_usuariosadm, container, false)

        // Inicializa as views
        voltarInicioTextView = view.findViewById(R.id.voltar_inicio)
        tituloGerenciamentoTextView = view.findViewById(R.id.titulo_gerenciamento)
        tipoUsuario1Spinner = view.findViewById(R.id.tipo_usuario_1)
        tipoUsuario2Spinner = view.findViewById(R.id.tipo_usuario_2)
        salvarButton1 = view.findViewById(R.id.salvar_button_1)
        salvarButton2 = view.findViewById(R.id.salvar_button_2)
        excluirButton1 = view.findViewById(R.id.excluir_button_1)
        excluirButton2 = view.findViewById(R.id.excluir_button_2)

        // Configura os Spinners
        setupSpinners()

        // Configura os listeners dos botões
        setupListeners()

        // Ação do link "Cadastre-se"
        voltarInicioTextView.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }

        return view
    }

    private fun setupSpinners() {
        // Configura o Spinner 1
        val adapter1 = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipos_usuario,
            android.R.layout.simple_spinner_item
        )
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tipoUsuario1Spinner.adapter = adapter1

        // Configura o Spinner 2
        val adapter2 = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipos_usuario,
            android.R.layout.simple_spinner_item
        )
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tipoUsuario2Spinner.adapter = adapter2
    }

    private fun setupListeners() {
        salvarButton1.setOnClickListener {
            // Lógica para salvar o usuário 1
            val tipoUsuario = tipoUsuario1Spinner.selectedItem.toString()
            salvarUsuario(1, tipoUsuario)
        }

        salvarButton2.setOnClickListener {
            // Lógica para salvar o usuário 2
            val tipoUsuario = tipoUsuario2Spinner.selectedItem.toString()
            salvarUsuario(2, tipoUsuario)
        }

        excluirButton1.setOnClickListener {
            // Lógica para excluir o usuário 1
            excluirUsuario(1)
        }

        excluirButton2.setOnClickListener {
            // Lógica para excluir o usuário 2
            excluirUsuario(2)
        }
    }

    private fun salvarUsuario(usuarioId: Int, tipoUsuario: String) {
        // Implemente a lógica para salvar o usuário (ex: em um banco de dados ou lista)
        println("Usuário $usuarioId salvo com tipo: $tipoUsuario")
    }

    private fun excluirUsuario(usuarioId: Int) {
        // Implemente a lógica para excluir o usuário
        println("Usuário $usuarioId excluído.")
    }
}
