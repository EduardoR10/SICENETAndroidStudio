package com.example.sicenet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sicenet.data.local.entity.AlumnoEntity

@Dao
interface AlumnoDao {
    @Query("SELECT * FROM perfil_alumno LIMIT 1")
    suspend fun getPerfil(): AlumnoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerfil(alumno: AlumnoEntity)

    @Query("DELETE FROM perfil_alumno")
    suspend fun clearPerfil()
}