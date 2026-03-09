package com.example.sicenet.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.sicenet.data.SessionManager
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.LocalDataSource
import com.example.sicenet.data.local.SicenetDatabase
import com.example.sicenet.data.local.entity.*
import com.example.sicenet.data.work.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SicenetViewModel(application: Application) : AndroidViewModel(application) {

    init {
        SessionManager.init(application)
    }

    private val db = SicenetDatabase.getDatabase(application)
    private val localDataSource = LocalDataSource(
        db.cargaAcademicaDao(),
        db.alumnoDao(),
        db.kardexDao(),
        db.califUnidadDao(),
        db.califFinalDao()
    )
    private val repository = SicenetRepository(localDataSource)

    var matricula by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var infoMessage by mutableStateOf<String?>(null)

    var isLoggedIn by mutableStateOf(false)
    var alumno by mutableStateOf<Alumno?>(null)

    // TODAS LAS LISTAS DEL ESTUDIANTE
    var cargaAcademica by mutableStateOf<List<CargaAcademicaEntity>>(emptyList())
    var kardexData by mutableStateOf<List<KardexEntity>>(emptyList())
    var califUnidadesData by mutableStateOf<List<CalifUnidadEntity>>(emptyList())
    var califFinalesData by mutableStateOf<List<CalifFinalEntity>>(emptyList())

    var currentScreen by mutableStateOf("PERFIL")
    var ultimaActualizacion by mutableStateOf("No sincronizado")

    var hasLastSession by mutableStateOf(SessionManager.hasLastSession())

    fun onLoginClick() {
        if (matricula.isBlank() || password.isBlank()) {
            errorMessage = "Llena todos los campos"
            return
        }
        iniciarSesion(matricula, password, isLastSession = false)
    }

    fun onLastSessionClick() {
        val lastMat = SessionManager.getLastMatricula()
        val lastPass = SessionManager.getLastPassword()
        iniciarSesion(lastMat, lastPass, isLastSession = true)
    }

    private fun iniciarSesion(mat: String, pass: String, isLastSession: Boolean) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            infoMessage = null

            val loginResult = repository.login(mat, pass)

            if (loginResult.isSuccess) {
                SessionManager.saveCredentials(mat, pass)
                hasLastSession = true
                fetchPerfil()
            } else {
                if (isLastSession) {
                    cargarPerfilOffline()
                } else {
                    isLoading = false
                    errorMessage = "Error de Login: Verifica credenciales o conexión."
                }
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
                    val alumnoParsed = AlumnoParser.parse(jsonString)
                    alumno = alumnoParsed
                    isLoggedIn = true
                    infoMessage = null
                    localDataSource.savePerfil(alumnoParsed.toEntity())
                } catch (e: Exception) {
                    errorMessage = "Error al procesar datos del alumno"
                }
            } else {
                cargarPerfilOffline()
            }
        }
    }

    private fun cargarPerfilOffline() {
        viewModelScope.launch {
            val perfilLocal = localDataSource.getPerfil()
            isLoading = false
            if (perfilLocal != null) {
                alumno = perfilLocal.toUiModel()
                isLoggedIn = true
                infoMessage = "Modo Sin Conexión (Mostrando datos locales)"
            } else {
                errorMessage = "Aún no ha sido cargada para visualizar sin internet."
            }
        }
    }

    fun logout() {
        isLoggedIn = false
        alumno = null
        errorMessage = null
        infoMessage = null
        password = ""
        cargaAcademica = emptyList()
        kardexData = emptyList()
        califUnidadesData = emptyList()
        califFinalesData = emptyList()
        currentScreen = "PERFIL"
        SessionManager.sessionCookie = null
    }

    fun obtenerCargaAcademica() {
        val workManager = WorkManager.getInstance(getApplication())
        val fetchRequest = OneTimeWorkRequestBuilder<FetchCargaWorker>().build()
        val saveRequest = OneTimeWorkRequestBuilder<SaveCargaWorker>().build()

        workManager.beginWith(fetchRequest).then(saveRequest).enqueue()

        workManager.getWorkInfoByIdLiveData(saveRequest.id).observeForever { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                viewModelScope.launch {
                    cargaAcademica = localDataSource.getCargaAcademica()
                    actualizarFecha()
                    infoMessage = null
                }
            } else if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                viewModelScope.launch {
                    val localData = localDataSource.getCargaAcademica()
                    if (localData.isNotEmpty()) {
                        cargaAcademica = localData
                        infoMessage = "Modo Sin Conexión (Carga Local)"
                    } else {
                        cargaAcademica = emptyList()
                    }
                }
            }
        }
    }

    fun obtenerKardex() {
        val workManager = WorkManager.getInstance(getApplication())
        val fetchRequest = OneTimeWorkRequestBuilder<FetchKardexWorker>().build()
        val saveRequest = OneTimeWorkRequestBuilder<SaveKardexWorker>().build()

        workManager.beginWith(fetchRequest).then(saveRequest).enqueue()

        workManager.getWorkInfoByIdLiveData(saveRequest.id).observeForever { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                viewModelScope.launch {
                    kardexData = localDataSource.getKardex()
                    actualizarFecha()
                    infoMessage = null
                }
            } else if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                viewModelScope.launch {
                    val localKardex = localDataSource.getKardex()
                    if (localKardex.isNotEmpty()) {
                        kardexData = localKardex
                        infoMessage = "Modo Sin Conexión (Kardex Local)"
                    } else {
                        kardexData = emptyList()
                    }
                }
            }
        }
    }

    fun obtenerCalificacionesUnidades() {
        val workManager = WorkManager.getInstance(getApplication())
        val fetchRequest = OneTimeWorkRequestBuilder<FetchCalifUnidadesWorker>().build()
        val saveRequest = OneTimeWorkRequestBuilder<SaveCalifUnidadesWorker>().build()

        workManager.beginWith(fetchRequest).then(saveRequest).enqueue()

        workManager.getWorkInfoByIdLiveData(saveRequest.id).observeForever { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                viewModelScope.launch {
                    califUnidadesData = localDataSource.getCalifUnidades()
                    actualizarFecha()
                    infoMessage = null
                }
            } else if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                viewModelScope.launch {
                    val localData = localDataSource.getCalifUnidades()
                    if (localData.isNotEmpty()) {
                        califUnidadesData = localData
                        infoMessage = "Modo Sin Conexión (Datos Locales)"
                    } else {
                        califUnidadesData = emptyList()
                    }
                }
            }
        }
    }

    fun obtenerCalificacionesFinales() {
        val workManager = WorkManager.getInstance(getApplication())
        val fetchRequest = OneTimeWorkRequestBuilder<FetchCalifFinalesWorker>().build()
        val saveRequest = OneTimeWorkRequestBuilder<SaveCalifFinalesWorker>().build()

        workManager.beginWith(fetchRequest).then(saveRequest).enqueue()

        workManager.getWorkInfoByIdLiveData(saveRequest.id).observeForever { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                viewModelScope.launch {
                    califFinalesData = localDataSource.getCalifFinales()
                    actualizarFecha()
                    infoMessage = null
                }
            } else if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                viewModelScope.launch {
                    val localData = localDataSource.getCalifFinales()
                    if (localData.isNotEmpty()) {
                        califFinalesData = localData
                        infoMessage = "Modo Sin Conexión (Datos Locales)"
                    } else {
                        califFinalesData = emptyList()
                    }
                }
            }
        }
    }

    private fun actualizarFecha() {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        ultimaActualizacion = sdf.format(Date())
    }
}