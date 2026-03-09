package com.example.sicenet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calif_finales")
data class CalifFinalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String,
    val grupo: String,
    val calif: Int,
    val acreditacion: String,
    val observaciones: String
)