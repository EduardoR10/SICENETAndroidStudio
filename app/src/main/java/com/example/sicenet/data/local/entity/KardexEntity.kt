package com.example.sicenet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kardex")
data class KardexEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clvMat: String,
    val clvOfiMat: String,
    val materia: String,
    val cdts: Int,
    val calif: Int,
    val acreditacion: String,
    val periodo: String,
    val promedioGral: Double,
    val avanceCdts: Double
)