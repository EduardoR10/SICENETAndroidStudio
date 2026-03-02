package com.example.sicenet.data

import com.example.sicenet.data.local.LocalDataSource
import com.example.sicenet.data.local.entity.CargaAcademicaEntity
import com.example.sicenet.data.remote.AcademicDataSource
import com.example.sicenet.data.remote.AuthDataSource
import com.example.sicenet.data.remote.CargaAcademicaParser

// Se inyecta LocalDataSource para guardar en Room
class SicenetRepository(private val localDataSource: LocalDataSource) {

    private val authDataSource = AuthDataSource()
    private val academicDataSource = AcademicDataSource()

    suspend fun login(matricula: String, contrasenia: String): Result<Boolean> {
        return authDataSource.login(matricula, contrasenia)
    }

    suspend fun getAlumnoAcademico(): Result<String> {
        return academicDataSource.getAlumnoAcademico()
    }

    // Flujo offline-first básico: Intenta red -> Guarda local -> Devuelve.
    // Si falla red, devuelve local. (WorkManager lo automatizará después).
    suspend fun getCargaAcademica(): Result<List<CargaAcademicaEntity>> {
        val remoteResult = academicDataSource.getCargaAcademica()

        if (remoteResult.isSuccess) {
            val jsonString = remoteResult.getOrDefault("[]")
            val entidades = CargaAcademicaParser.parse(jsonString)
            // Guarda en Room
            localDataSource.saveCargaAcademica(entidades)
            return Result.success(entidades)
        } else {
            // Si falla la red, lee de Room
            val localData = localDataSource.getCargaAcademica()
            if (localData.isNotEmpty()) {
                return Result.success(localData)
            }
            return Result.failure(Exception("Sin internet y sin datos locales guardados."))
        }
    }
}