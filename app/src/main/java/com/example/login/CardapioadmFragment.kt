package com.example.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide

class CardapioadmFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var tvVoltar: TextView
    private lateinit var spinner: Spinner
    private lateinit var btnEscolherFoto: Button
    private lateinit var tvFotoSelecionada: TextView
    private lateinit var etNome: EditText
    private lateinit var etDescricao: EditText
    private lateinit var etPreco: EditText
    private lateinit var btnCadastrar: Button
    private lateinit var layoutProdutosCadastrados: LinearLayout
    private var tipoProdutoSelecionado: String = ""
    private var fotoUri: Uri? = null
    // Variável para rastrear o formulário de edição atual
    private var formularioEdicaoAtual: View? = null


    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        // Inflar o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_cardapioadm, container, false)

        tvVoltar = view.findViewById(R.id.tvVoltar)
        spinner = view.findViewById(R.id.spinner_tipo_produto)
        btnEscolherFoto = view.findViewById(R.id.btnEscolherFoto)
        tvFotoSelecionada = view.findViewById(R.id.tvFotoSelecionada)
        etNome = view.findViewById(R.id.etNome)
        etDescricao = view.findViewById(R.id.etDescricao)
        etPreco = view.findViewById(R.id.etPreco)
        btnCadastrar = view.findViewById(R.id.btnCadastrar)
        layoutProdutosCadastrados = view.findViewById(R.id.layoutProdutosCadastrados)

        // Criando um ArrayAdapter usando o array de string e o layout padrão do spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipos_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                tipoProdutoSelecionado = parent.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Selecionado: $tipoProdutoSelecionado", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                tipoProdutoSelecionado = ""
            }
        }

        // Ação do link "Cadastre-se"
        tvVoltar.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }

        btnEscolherFoto.setOnClickListener {
            abrirGaleria()
        }

        btnCadastrar.setOnClickListener {
            if (fotoUri != null) {
                fazerUploadDaFoto()
            } else {
                cadastrarProduto(null) // Cadastra sem foto caso não seja selecionada
            }
        }

        carregarProdutosCadastrados()
        return view
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        fotoUri = uri
        tvFotoSelecionada.text = "Foto selecionada!"
    }

    private fun abrirGaleria() {
        pickImageLauncher.launch("image/*")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            fotoUri = data?.data
            tvFotoSelecionada.text = "Foto selecionada!"
        }
    }

    private fun fazerUploadDaFoto() {
        val nomeArquivo = "produtos/${UUID.randomUUID()}.jpg"
        val fotoRef = storage.reference.child(nomeArquivo)

        fotoUri?.let { uri ->
            fotoRef.putFile(uri)
                .addOnSuccessListener {
                    fotoRef.downloadUrl.addOnSuccessListener { uri ->
                        cadastrarProduto(uri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Erro ao fazer upload da foto.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun cadastrarProduto(urlFoto: String?) {
        val nome = etNome.text.toString().trim()
        val descricao = etDescricao.text.toString().trim()
        val preco = etPreco.text.toString().trim()

        if (nome.isEmpty() || descricao.isEmpty() || preco.isEmpty() || tipoProdutoSelecionado.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val produto: Map<String, Any> = hashMapOf(
            "nome" to nome,
            "descricao" to descricao,
            "preco" to preco.toDouble(),
            "tipo" to tipoProdutoSelecionado,
            "foto" to (urlFoto ?: "") // Garantir que seja String não nulo
        )


        db.collection("consumiveis")
            .add(produto)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                adicionarProdutoNaLista(produto)
                limparCampos()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao cadastrar produto.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarProdutosCadastrados() {
        layoutProdutosCadastrados.removeAllViews() // Limpa o layout antes de recarregar os produtos
        db.collection("consumiveis")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val produto = document.data
                    produto["id"] = document.id // Salva o ID para futuras operações
                    adicionarProdutoNaLista(produto)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao carregar produtos.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun adicionarProdutoNaLista(produto: Map<String, Any>) {
        // Certifique-se de que o produto possui um ID
        if (!produto.containsKey("id")) return
        val produtoView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_produto, layoutProdutosCadastrados, false) // Use um layout específico para itens de produto

        produtoView.findViewById<TextView>(R.id.nomeProduto).text = produto["nome"].toString()
        produtoView.findViewById<TextView>(R.id.descricaoProduto).text = produto["descricao"].toString()
        // Formatando o preço com duas casas decimais
        val preco = produto["preco"]?.toString()?.toDoubleOrNull() ?: 0.0
        val precoFormatado = String.format(Locale.getDefault(), "R$ %.2f", preco).replace(".", ",")
        produtoView.findViewById<TextView>(R.id.precoProduto).text = "Preço: $precoFormatado"
        produtoView.findViewById<TextView>(R.id.tipoProduto).text = "Tipo: ${produto["tipo"].toString()}"

        val fotoUrl = produto["foto"] as? String
        val imageView = produtoView.findViewById<ImageView>(R.id.imagemProduto)
        if (fotoUrl != null) {
            Glide.with(this).load(fotoUrl).into(imageView)
        }
        val btnEditar = produtoView.findViewById<Button>(R.id.btnEditar)
        val btnExcluir = produtoView.findViewById<Button>(R.id.btnExcluir)

        btnEditar.setOnClickListener {
            // Verifica se já existe um formulário de edição aberto
            if (formularioEdicaoAtual != null) {
                // Remove o formulário existente se o botão Editar for pressionado novamente
                layoutProdutosCadastrados.removeView(formularioEdicaoAtual)
                formularioEdicaoAtual = null
            } else {
                // Cria e exibe o formulário de edição
                val layoutFormularioEdicao = LayoutInflater.from(requireContext())
                    .inflate(R.layout.formulario_editar_produto, layoutProdutosCadastrados, false) as LinearLayout

                val btnSalvar = layoutFormularioEdicao.findViewById<Button>(R.id.btnSalvarEdicao)
                val etNovoNome = layoutFormularioEdicao.findViewById<EditText>(R.id.etNovoNome)
                val etNovaDescricao = layoutFormularioEdicao.findViewById<EditText>(R.id.etNovaDescricao)
                val etNovoPreco = layoutFormularioEdicao.findViewById<EditText>(R.id.etNovoPreco)
                val etNovoTipo = layoutFormularioEdicao.findViewById<EditText>(R.id.etNovoTipo)

                // Preenchendo o formulário com os dados atuais do produto
                etNovoNome.setText(produto["nome"].toString())
                etNovaDescricao.setText(produto["descricao"].toString())
                etNovoPreco.setText(precoFormatado) // Exibindo o preço formatado
                etNovoTipo.setText(produto["tipo"].toString())

                btnSalvar.setOnClickListener {
                    val novoNome = etNovoNome.text.toString()
                    val novaDescricao = etNovaDescricao.text.toString()
                    val novoPreco = etNovoPreco.text.toString().replace(",", ".").toDoubleOrNull() ?: 0.00
                    val novoTipo = etNovoTipo.text.toString()

                    atualizarProduto(produto["id"].toString(), novoNome, novaDescricao, novoPreco, novoTipo)

                    // Esconde o formulário de edição após salvar
                    layoutFormularioEdicao.visibility = View.GONE
                    formularioEdicaoAtual = null
                }

                // Obtendo o índice da produtoView e adicionando o formulário logo abaixo
                val produtoIndex = layoutProdutosCadastrados.indexOfChild(produtoView)
                layoutProdutosCadastrados.addView(layoutFormularioEdicao, produtoIndex + 1)
                formularioEdicaoAtual = layoutFormularioEdicao
            }
        }

        btnExcluir.setOnClickListener {
            // Chame a função para excluir o produto
            excluirProduto(produto["id"].toString())
        }

        layoutProdutosCadastrados.addView(produtoView)
    }

    private fun limparCampos() {
        etNome.text.clear()
        etDescricao.text.clear()
        etPreco.text.clear()
        spinner.setSelection(0)
        tvFotoSelecionada.text = ""
        fotoUri = null
    }

    private fun atualizarProduto(produtoId: String, nome: String, descricao: String, preco: Double, tipo: String) {
        val produtoAtualizado = hashMapOf(
            "nome" to nome,
            "descricao" to descricao,
            "preco" to preco,
            "tipo" to tipo
        )

        if (fotoUri != null) {
            val nomeArquivo = "produtos/${UUID.randomUUID()}.jpg"
            val fotoRef = storage.reference.child(nomeArquivo)

            fotoRef.putFile(fotoUri!!)
                .addOnSuccessListener {
                    fotoRef.downloadUrl.addOnSuccessListener { uri ->
                        produtoAtualizado["foto"] = uri.toString()
                        salvarProdutoAtualizado(produtoId, produtoAtualizado)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Erro ao fazer upload da nova foto.", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Caso fotoUri seja null, mantenha a foto existente
            db.collection("consumiveis").document(produtoId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.contains("foto")) {
                        produtoAtualizado["foto"] = document["foto"].toString()
                    }
                    salvarProdutoAtualizado(produtoId, produtoAtualizado)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Erro ao recuperar a foto existente.", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun salvarProdutoAtualizado(produtoId: String, produtoAtualizado: Map<String, Any>) {
        db.collection("consumiveis").document(produtoId)
            .set(produtoAtualizado)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Produto atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                carregarProdutosCadastrados() // Atualiza a lista de produtos
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao atualizar produto.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun excluirProduto(produtoId: String) {
        db.collection("consumiveis").document(produtoId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show()
                carregarProdutosCadastrados() // Recarrega os produtos para atualizar a lista
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao excluir produto.", Toast.LENGTH_SHORT).show()
            }
    }
}
