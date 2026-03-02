package com.example.sicenet.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.LocalDataSource
import com.example.sicenet.data.local.SicenetDatabase
import com.example.sicenet.data.local.entity.CargaAcademicaEntity
import kotlinx.coroutines.launch
import androidx.work.*
import com.example.sicenet.data.work.FetchCargaWorker
import com.example.sicenet.data.work.SaveCargaWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SicenetViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SicenetDatabase.getDatabase(application)
    private val localDataSource = LocalDataSource(db.cargaAcademicaDao())
    private val repository = SicenetRepository(localDataSource)

    var matricula by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var isLoggedIn by mutableStateOf(false)
    var alumno by mutableStateOf<Alumno?>(null)
    var cargaAcademica by mutableStateOf<List<CargaAcademicaEntity>>(emptyList())

    var currentScreen by mutableStateOf("PERFIL")
    var ultimaActualizacion by mutableStateOf("No sincronizado")

    fun onLoginClick() {
        if (matricula.isBlank() || password.isBlank()) {
            errorMessage = "Llena todos los campos"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val loginResult = repository.login(matricula, password)

            if (loginResult.isSuccess) {
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
        cargaAcademica = emptyList()
    }

    fun obtenerCargaAcademica() {
        val workManager = WorkManager.getInstance(getApplication())

        // 1. Crear las peticiones de trabajo únicas
        val fetchRequest = OneTimeWorkRequestBuilder<FetchCargaWorker>().build()
        val saveRequest = OneTimeWorkRequestBuilder<SaveCargaWorker>().build()

        // 2. Encadenarlas: Primero fetch, LUEGO save
        workManager.beginWith(fetchRequest)
            .then(saveRequest)
            .enqueue()

        // 3. Monitorear el estado (Como pide la rúbrica)
        workManager.getWorkInfoByIdLiveData(saveRequest.id).observeForever { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                viewModelScope.launch {
                    val db = SicenetDatabase.getDatabase(getApplication())
                    val localDataSource = LocalDataSource(db.cargaAcademicaDao())
                    cargaAcademica = localDataSource.getCargaAcademica()

                    // Actualizar fecha
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    ultimaActualizacion = sdf.format(Date())
                }
            } else if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                viewModelScope.launch {
                    val db = SicenetDatabase.getDatabase(getApplication())
                    val localDataSource = LocalDataSource(db.cargaAcademicaDao())
                    val localData = localDataSource.getCargaAcademica()
                    if (localData.isNotEmpty()) {
                        cargaAcademica = localData
                    } else {
                        errorMessage = "Sin internet y sin datos guardados."
                    }
                }
            }
        }
    }
}