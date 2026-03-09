package com.example.sicenet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sicenet.data.local.dao.*
import com.example.sicenet.data.local.entity.*

@Database(
    entities = [
        CargaAcademicaEntity::class,
        AlumnoEntity::class,
        KardexEntity::class,
        CalifUnidadEntity::class,
        CalifFinalEntity::class // NUEVA TABLA
    ],
    version = 5, // INCREMENTADO A 5
    exportSchema = false
)
abstract class SicenetDatabase : RoomDatabase() {

    abstract fun cargaAcademicaDao(): CargaAcademicaDao
    abstract fun alumnoDao(): AlumnoDao
    abstract fun kardexDao(): KardexDao
    abstract fun califUnidadDao(): CalifUnidadDao
    abstract fun califFinalDao(): CalifFinalDao

    companion object {
        @Volatile
        private var INSTANCE: SicenetDatabase? = null

        fun getDatabase(context: Context): SicenetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SicenetDatabase::class.java,
                    "sicenet_offline_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}