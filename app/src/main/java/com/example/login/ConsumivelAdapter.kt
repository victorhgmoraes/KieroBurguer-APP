package com.example.login

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ConsumivelAdapter(
    private val consumiveis: List<PecaaquiFragment.Consumivel>,
    private val context: Context,
    private val onAddToCart: (PecaaquiFragment.Consumivel) -> Unit // Callback para o carrinho
) : RecyclerView.Adapter<ConsumivelAdapter.ConsumivelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsumivelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
        return ConsumivelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsumivelViewHolder, position: Int) {
        val consumivel = consumiveis[position]
        holder.nome.text = consumivel.nome
        holder.descricao.text = consumivel.descricao
        holder.preco.text = "R$ ${consumivel.preco}"

        // Carregar a imagem da URL usando o Glide
        Glide.with(context)
            .load(consumivel.foto)
            .into(holder.foto)

        holder.adicionarCarrinho.setOnClickListener {
            onAddToCart(consumivel) // Chama a callback para adicionar ao carrinho
        }
    }


    override fun getItemCount(): Int = consumiveis.size

    class ConsumivelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.tvNomeProduto)
        val descricao: TextView = itemView.findViewById(R.id.tvDescricaoProduto)
        val preco: TextView = itemView.findViewById(R.id.tvPrecoProduto)
        val foto: ImageView = itemView.findViewById(R.id.ivFotoProduto)
        val adicionarCarrinho: Button = itemView.findViewById(R.id.btnAdicionarCarrinho)
    }
}
