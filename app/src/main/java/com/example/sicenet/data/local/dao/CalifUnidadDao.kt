package com.example.sicenet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.sicenet.data.local.entity.CalifUnidadEntity

@Dao
interface CalifUnidadDao {
    @Query("SELECT * FROM calif_unidades")
    suspend fun getCalifUnidades(): List<CalifUnidadEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalifUnidades(calificaciones: List<CalifUnidadEntity>)

    @Query("DELETE FROM calif_unidades")
    suspend fun clearCalifUnidades()

    @Transaction
    suspend fun updateCalifUnidades(calificaciones: List<CalifUnidadEntity>) {
        clearCalifUnidades()
        insertCalifUnidades(calificaciones)
    }
}