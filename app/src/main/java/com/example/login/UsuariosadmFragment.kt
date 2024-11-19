package com.example.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class UsuariosadmFragment : Fragment() {

    private lateinit var voltarInicioTextView: TextView
    private lateinit var tituloGerenciamentoTextView: TextView
    private lateinit var tabelaUsuarios: TableLayout
    private lateinit var db: FirebaseFirestore

    // Lista para armazenar as linhas dos usuários
    private val usuariosLinhas: MutableList<TableRow> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o Firestore
        db = FirebaseFirestore.getInstance()

        // Carregar usuários do Firestore
        carregarUsuarios()
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
        tabelaUsuarios = view.findViewById(R.id.tabela_usuarios)

        // Ação do link "Cadastre-se"
        voltarInicioTextView.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }

        return view
    }

    private fun carregarUsuarios() {
        // Limpar as linhas da tabela apenas removendo as views associadas
        tabelaUsuarios.removeViews(1, tabelaUsuarios.childCount - 1) // Mantém a primeira linha (cabeçalho) intacta
        usuariosLinhas.clear() // Limpa a lista interna de linhas

        db.collection("tbl_login")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject<Usuario>()
                    user.documentId = document.id // Salva o ID do documento
                    adicionarLinhaUsuario(user) // Adiciona a linha de usuário à tabela
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Erro ao carregar usuários: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun adicionarLinhaUsuario(usuario: Usuario) {
        val inflater = LayoutInflater.from(context)
        val linhaView = inflater.inflate(R.layout.item_usuario, tabelaUsuarios, false) as TableRow

        // Atribui os dados aos elementos da linha
        linhaView.findViewById<TextView>(R.id.tvUsuario).text = usuario.User_Login
        linhaView.findViewById<TextView>(R.id.tvNome).text = usuario.Nome_Login
        linhaView.findViewById<TextView>(R.id.tvEmail).text = usuario.Email_Login

        val spinnerTipo = linhaView.findViewById<Spinner>(R.id.spinnerTipoUsuario)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipos_usuario,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapter

        // Seleciona o tipo de usuário atual no Spinner
        val tipoIndex = resources.getStringArray(R.array.tipos_usuario).indexOf(usuario.Tipo_Login)
        spinnerTipo.setSelection(tipoIndex)

        linhaView.findViewById<Button>(R.id.btnSalvar).setOnClickListener {
            val novoTipo = spinnerTipo.selectedItem.toString()
            salvarUsuario(usuario.User_Login, novoTipo)
        }

        linhaView.findViewById<Button>(R.id.btnExcluir).setOnClickListener {
            excluirUsuario(usuario)
        }

        // Adiciona a linha à lista
        usuariosLinhas.add(linhaView)
        tabelaUsuarios.addView(linhaView)
    }

    private fun salvarUsuario(uid: String, novoTipo: String) {
        // Converter o tipo para número (0 ou 1)
        val tipoUsuario = if (novoTipo == "Admin") 1 else 0

        db.collection("tbl_login")
            .whereEqualTo("User_Login", uid)
            .get()
            .addOnSuccessListener { documents ->
                // Verifica se a lista de documentos não está vazia
                if (documents.documents.isNotEmpty()) {
                    val documentId = documents.documents[0].id
                    db.collection("tbl_login").document(documentId)
                        .update("Tipo_Usuario", tipoUsuario) // Atualiza com número (0 ou 1)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Usuário atualizado com sucesso.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Erro ao atualizar usuário: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Erro: usuário não encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao buscar usuário: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun excluirUsuario(usuario: Usuario) {
        db.collection("tbl_login")
            .document(usuario.documentId ?: "")
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Usuário excluído com sucesso.", Toast.LENGTH_SHORT).show()
                carregarUsuarios() // Recarrega a lista para atualizar a tabela, sem duplicação
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao excluir usuário: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    data class Usuario(
        val User_Login: String = "",
        val Nome_Login: String = "",
        val Email_Login: String = "",
        val Tipo_Login: String = "",
        var documentId: String? = null
    )
}
