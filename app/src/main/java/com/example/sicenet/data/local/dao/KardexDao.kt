package com.example.sicenet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.sicenet.data.local.entity.KardexEntity

@Dao
interface KardexDao {
    @Query("SELECT * FROM kardex")
    suspend fun getKardex(): List<KardexEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKardex(kardex: List<KardexEntity>)

    @Query("DELETE FROM kardex")
    suspend fun clearKardex()

    @Transaction
    suspend fun updateKardex(kardex: List<KardexEntity>) {
        clearKardex()
        insertKardex(kardex)
    }
}