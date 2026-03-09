package com.example.sicenet.data.local

import com.example.sicenet.data.local.dao.*
import com.example.sicenet.data.local.entity.*

class LocalDataSource(
    private val cargaDao: CargaAcademicaDao,
    private val alumnoDao: AlumnoDao,
    private val kardexDao: KardexDao,
    private val califUnidadDao: CalifUnidadDao,
    private val califFinalDao: CalifFinalDao
) {
    // ---- Perfil Alumno ----
    suspend fun getPerfil(): AlumnoEntity? {
        return alumnoDao.getPerfil()
    }

    suspend fun savePerfil(alumno: AlumnoEntity) {
        alumnoDao.clearPerfil()
        alumnoDao.insertPerfil(alumno)
    }

    suspend fun clearPerfil() {
        alumnoDao.clearPerfil()
    }

    // ---- Carga Académica ----
    suspend fun getCargaAcademica(): List<CargaAcademicaEntity> {
        return cargaDao.getCargaAcademica()
    }

    suspend fun saveCargaAcademica(carga: List<CargaAcademicaEntity>) {
        cargaDao.updateCarga(carga)
    }

    // ---- Kardex ----
    suspend fun getKardex(): List<KardexEntity> {
        return kardexDao.getKardex()
    }

    suspend fun saveKardex(kardex: List<KardexEntity>) {
        kardexDao.updateKardex(kardex)
    }

    // ---- Calificaciones Unidades ----
    suspend fun getCalifUnidades(): List<CalifUnidadEntity> {
        return califUnidadDao.getCalifUnidades()
    }

    suspend fun saveCalifUnidades(calif: List<CalifUnidadEntity>) {
        califUnidadDao.updateCalifUnidades(calif)
    }

    // ---- Calificaciones Finales ----
    suspend fun getCalifFinales(): List<CalifFinalEntity> {
        return califFinalDao.getCalifFinales()
    }

    suspend fun saveCalifFinales(calif: List<CalifFinalEntity>) {
        califFinalDao.updateCalifFinales(calif)
    }
}