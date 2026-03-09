package com.example.sicenet.data

import com.example.sicenet.data.local.LocalDataSource
import com.example.sicenet.data.remote.AcademicDataSource
import com.example.sicenet.data.remote.AuthDataSource

class SicenetRepository(private val localDataSource: LocalDataSource? = null) {

    private val authDataSource = AuthDataSource()
    private val academicDataSource = AcademicDataSource()

    suspend fun login(matricula: String, contrasenia: String): Result<Boolean> {
        return authDataSource.login(matricula, contrasenia)
    }

    suspend fun getAlumnoAcademico(): Result<String> {
        return academicDataSource.getAlumnoAcademico()
    }

    suspend fun getCargaAcademicaRemota(): Result<String> {
        return academicDataSource.getCargaAcademica()
    }

    suspend fun getKardexRemoto(lineamiento: Int = 3): Result<String> {
        return academicDataSource.getKardex(lineamiento)
    }

    suspend fun getCalifUnidadesRemotas(): Result<String> {
        return academicDataSource.getCalifUnidades()
    }

    // NUEVO PUENTE PARA FINALES
    suspend fun getCalifFinalesRemotas(modEducativo: Int = 1): Result<String> {
        return academicDataSource.getCalifFinales(modEducativo)
    }
}