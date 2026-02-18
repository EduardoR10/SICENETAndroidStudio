package com.example.sicenet.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicenet.data.SicenetRepository
import kotlinx.coroutines.launch
import com.example.sicenet.ui.Alumno
import com.example.sicenet.ui.AlumnoParser

class SicenetViewModel : ViewModel() {

    // Instancia del repositorio
    private val repository = SicenetRepository()


    var matricula by mutableStateOf("")
    var password by mutableStateOf("")


    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)


    var isLoggedIn by mutableStateOf(false)

    var alumno by mutableStateOf<Alumno?>(null)


    fun onLoginClick() {
        if (matricula.isBlank() || password.isBlank()) {
            errorMessage = "Llena todos los campos"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            //Intentar Login
            val loginResult = repository.login(matricula, password)

            if (loginResult.isSuccess) {
                //Si es exitoso, marcamos login y buscamos el perfil
                //La cookie ya se guardó sola en el SessionManager dentro del repo
                fetchPerfil()
            } else {
                isLoading = false
                errorMessage = "Error de Login: ${loginResult.exceptionOrNull()?.message}"
            }
        }
    }

    private fun fetchPerfil() {
        viewModelScope.launch {
            val perfilResult = repository.getAlumnoAcademico()
            isLoading = false

            if (perfilResult.isSuccess) {
                val jsonString = perfilResult.getOrDefault("{}")
                try {
                    //Convertimos el JSON a objeto
                    alumno = AlumnoParser.parse(jsonString)
                    isLoggedIn = true
                } catch (e: Exception) {
                    errorMessage = "Error al procesar datos del alumno"
                }
            } else {
                errorMessage = "Error al cargar perfil: ${perfilResult.exceptionOrNull()?.message}"
            }
        }
    }

    fun logout() {
        isLoggedIn = false
        alumno = null
        errorMessage = null
        password = ""
    }
}

