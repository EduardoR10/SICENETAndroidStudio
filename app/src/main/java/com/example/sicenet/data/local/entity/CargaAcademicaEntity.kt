package com.example.sicenet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carga_academica")
data class CargaAcademicaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String,
    val docente: String,
    val clvOficial: String,
    val grupo: String,
    val creditos: Int,
    val estadoMateria: String,
    val lunes: String,
    val martes: String,
    val miercoles: String,
    val jueves: String,
    val viernes: String,
    val sabado: String,
    val observaciones: String
)