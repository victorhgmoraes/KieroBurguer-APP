package com.example.login

import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PecaaquiFragmentTest {

    private lateinit var fragment: PecaaquiFragment

    @Before
    fun setUp() {

        fragment = PecaaquiFragment()
        fragment.tvTotalPreco = TextView(ApplicationProvider.getApplicationContext()) // Simula o TextView
        fragment.adapterCarrinho = ConsumivelAdapter(mutableListOf(), ApplicationProvider.getApplicationContext()) {}
    }

    @Test
    fun testLimparCarrinho() {
        // Adiciona itens fictícios no carrinho
        fragment.carrinho.add(PecaaquiFragment.Consumivel(nome = "Hamburguer", preco = 15.0))
        fragment.carrinho.add(PecaaquiFragment.Consumivel(nome = "Bebida", preco = 5.0))
        fragment.totalPreco = 20.0
        fragment.tvTotalPreco.text = "Total: R$ 20.00"

        // Chama a função que será testada
        fragment.limparCarrinho()

        // Verifica se o carrinho está vazio
        assertTrue(fragment.carrinho.isEmpty())

        // Verifica se o preço total foi zerado
        assertEquals(0.0, fragment.totalPreco, 0.01)

        // Verifica se o texto foi atualizado corretamente
        assertEquals("Total: R$ 0.00", fragment.tvTotalPreco.text.toString())
    }

    // TESTES FUNCIONAIS ABAIXO

    @Test
    fun testAddToCart() {
        // Simula a adição de um item ao carrinho
        val consumivel = PecaaquiFragment.Consumivel(nome = "Hamburguer", preco = 15.0)
        fragment.addToCart(consumivel)

        // Verifica se o item foi adicionado ao carrinho
        assertTrue(fragment.carrinho.contains(consumivel))

        // Verifica se o preço total foi atualizado corretamente
        assertEquals(15.0, fragment.totalPreco, 0.01)

        // Verifica se o texto foi atualizado corretamente
        assertEquals("Total: R$ 15.00", fragment.tvTotalPreco.text.toString())
    }
    @Test
    fun testLimparCarrinhoQuandoVazio() {
        // Certifique-se de que o carrinho está vazio antes de realizar o teste
        fragment.carrinho.clear()
        fragment.totalPreco = 0.0
        fragment.tvTotalPreco.text = "Total: R$ 0.00"

        // Simula o clique no botão "Limpar Carrinho"
        fragment.limparCarrinho()

        // Verifica se o carrinho ainda está vazio após chamar a função
        assertTrue(fragment.carrinho.isEmpty())

        // Verifica se o preço total não foi alterado
        assertEquals(0.0, fragment.totalPreco, 0.01)

        // Verifica se o texto do preço total continua "R$ 0.00"
        assertEquals("Total: R$ 0.00", fragment.tvTotalPreco.text.toString())
    }


}
