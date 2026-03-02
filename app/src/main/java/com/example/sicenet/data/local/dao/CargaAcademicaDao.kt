package com.example.sicenet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.sicenet.data.local.entity.CargaAcademicaEntity

@Dao
interface CargaAcademicaDao {
    @Query("SELECT * FROM carga_academica")
    suspend fun getCargaAcademica(): List<CargaAcademicaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarga(carga: List<CargaAcademicaEntity>)

    @Query("DELETE FROM carga_academica")
    suspend fun clearCarga()

    // Reemplaza toda la tabla con los datos más nuevos
    @Transaction
    suspend fun updateCarga(carga: List<CargaAcademicaEntity>) {
        clearCarga()
        insertCarga(carga)
    }
}