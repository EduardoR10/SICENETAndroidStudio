package com.example.sicenet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sicenet.data.local.dao.CargaAcademicaDao
import com.example.sicenet.data.local.entity.CargaAcademicaEntity

@Database(entities = [CargaAcademicaEntity::class], version = 1, exportSchema = false)
abstract class SicenetDatabase : RoomDatabase() {

    abstract fun cargaAcademicaDao(): CargaAcademicaDao

    companion object {
        @Volatile
        private var INSTANCE: SicenetDatabase? = null

        fun getDatabase(context: Context): SicenetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SicenetDatabase::class.java,
                    "sicenet_offline_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}