package com.example.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class CardapioadmFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_cardapioadm, container, false)

        val tvVoltar: TextView = view.findViewById(R.id.tvVoltar)
        // Configuração do Spinner
        val spinner: Spinner = view.findViewById(R.id.spinner_tipo_produto)

        // Criando um ArrayAdapter usando o array de string e o layout padrão do spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipos_array, // O array de opções que você deve definir no strings.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Definir o layout para usar quando a lista de opções aparecer
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar o adapter ao spinner
            spinner.adapter = adapter
        }

        // Definir ação quando um item do spinner for selecionado
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Aqui você pode realizar ações baseadas no item selecionado
                val itemSelecionado = parent.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Selecionado: $itemSelecionado", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Ação quando nada for selecionado, se necessário
            }
        }

        // Ação do link "Cadastre-se"
        tvVoltar.setOnClickListener {
            (activity as? LoginActivity)?.replaceFragment(HomeFragment())
        }

        return view
    }
}
