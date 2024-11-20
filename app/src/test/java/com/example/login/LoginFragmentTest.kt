package com.example.login

import android.text.Editable
import android.text.InputType
import android.view.View
import android.widget.EditText
import org.junit.Test
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.mockito.Mock
import androidx.fragment.app.testing.launchFragmentInContainer
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit


class LoginFragmentTest {

    private lateinit var fragment: LoginFragment

    @Mock
    lateinit var mockAuth: FirebaseAuth
    @Mock
    lateinit var mockDb: FirebaseFirestore
    @Mock
    lateinit var mockCollectionRef: CollectionReference
    @Mock
    lateinit var mockQuery: Query
    @Mock
    lateinit var mockView: View
    @Mock
    lateinit var etUsuario: EditText
    @Mock
    lateinit var etSenha: EditText
    @Mock
    lateinit var tvMostrarSenha: TextView
    @Mock
    lateinit var mockActivity: LoginActivity

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this) // Inicializa os mocks
        fragment = LoginFragment()
        fragment.auth = mockAuth
        fragment.db = mockDb

        // Mockando o FirebaseFirestore
        `when`(mockDb.collection(anyString())).thenReturn(mockCollectionRef)

        // Mockando o CollectionReference para retornar um Query
        `when`(mockCollectionRef.whereEqualTo(anyString(), any())).thenReturn(mockQuery)

        // Mockando o Editable
        val editableUsuario = mock(Editable::class.java)
        `when`(editableUsuario.toString()).thenReturn("usuario_teste")
        `when`(etUsuario.text).thenReturn(editableUsuario)

        val editableSenha = mock(Editable::class.java)
        `when`(editableSenha.toString()).thenReturn("senha_teste")
        `when`(etSenha.text).thenReturn(editableSenha)
    }



    @Test
    fun testUsuarioNaoVazio() {
        val usuario = etUsuario.text.toString().trim()

        assertTrue(usuario.isNotEmpty())
    }

    @Test
    fun testSenhaNaoVazia() {
        val senha = etSenha.text.toString().trim()

        assertTrue(senha.isNotEmpty())
    }

}
