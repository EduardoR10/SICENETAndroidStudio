package com.example.sicenet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil_alumno")
data class AlumnoEntity(
    @PrimaryKey val matricula: String,
    val nombre: String,
    val carrera: String,
    val especialidad: String,
    val semestre: Int,
    val creditosAcumulados: Int,
    val creditosActuales: Int,
    val estatus: String,
    val inscrito: String,
    val fotoUrl: String
)