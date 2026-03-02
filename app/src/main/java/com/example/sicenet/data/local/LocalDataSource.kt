package com.example.sicenet.data.local

import com.example.sicenet.data.local.dao.CargaAcademicaDao
import com.example.sicenet.data.local.entity.CargaAcademicaEntity

class LocalDataSource(private val cargaDao: CargaAcademicaDao) {

    suspend fun getCargaAcademica(): List<CargaAcademicaEntity> {
        return cargaDao.getCargaAcademica()
    }

    suspend fun saveCargaAcademica(carga: List<CargaAcademicaEntity>) {
        cargaDao.updateCarga(carga)
    }
}