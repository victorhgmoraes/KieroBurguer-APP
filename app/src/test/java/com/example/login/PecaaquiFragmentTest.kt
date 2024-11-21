package com.example.login

import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
}
