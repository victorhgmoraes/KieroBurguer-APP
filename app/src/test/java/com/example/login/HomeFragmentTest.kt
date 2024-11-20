package com.example.login
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
@RunWith(MockitoJUnitRunner::class)
class HomeFragmentTest {

    @Mock
    lateinit var mockAuth: FirebaseAuth

    @Mock
    lateinit var mockFirestore: FirebaseFirestore

    private lateinit var homeFragment: HomeFragment

    @Before
    fun setup() {
        // Inicializa o fragmento
        homeFragment = HomeFragment()

        // Mocka dependências do Firebase
        homeFragment.auth = mockAuth
        homeFragment.db = mockFirestore

        // Mocka os elementos da interface
        homeFragment.tvWelcome = mock(TextView::class.java)
        homeFragment.btnLoginLogout = mock(Button::class.java)
    }

    @Test
    fun `updateUI should set button to Login when no user is logged in`() {
        // Simula que nenhum usuário está logado
        `when`(mockAuth.currentUser).thenReturn(null)

        // Chama o método que será testado
        homeFragment.updateUI()

        // Verifica se o texto do botão foi configurado para "Login"
        verify(homeFragment.btnLoginLogout).text = "Login"

        // Verifica se o TextView de boas-vindas foi escondido
        verify(homeFragment.tvWelcome).visibility = View.GONE
    }
}
