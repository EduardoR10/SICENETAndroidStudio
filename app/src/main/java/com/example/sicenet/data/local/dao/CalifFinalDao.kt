package com.example.sicenet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.sicenet.data.local.entity.CalifFinalEntity

@Dao
interface CalifFinalDao {
    @Query("SELECT * FROM calif_finales")
    suspend fun getCalifFinales(): List<CalifFinalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalifFinales(calificaciones: List<CalifFinalEntity>)

    @Query("DELETE FROM calif_finales")
    suspend fun clearCalifFinales()

    @Transaction
    suspend fun updateCalifFinales(calificaciones: List<CalifFinalEntity>) {
        clearCalifFinales()
        insertCalifFinales(calificaciones)
    }
}