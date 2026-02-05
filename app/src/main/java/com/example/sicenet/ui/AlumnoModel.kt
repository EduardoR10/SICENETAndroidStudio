package com.example.sicenet.ui

import org.json.JSONObject

data class Alumno(
    val matricula: String,
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

object AlumnoParser {
    fun parse(jsonString: String): Alumno {

        val json = JSONObject(jsonString)

        val nombreFoto = json.optString("urlFoto", "")
        val fullUrl = "https://sicenet.surguanajuato.tecnm.mx/fotos/$nombreFoto"

        return Alumno(
            matricula = json.optString("matricula"),
            nombre = json.optString("nombre"),
            carrera = json.optString("carrera"),
            especialidad = json.optString("especialidad"),
            semestre = json.optInt("semActual"),
            creditosAcumulados = json.optInt("cdtosAcumulados"),
            creditosActuales = json.optInt("cdtosActuales"),
            estatus = if(json.optString("estatus") == "VI") "VIGENTE" else json.optString("estatus"),
            inscrito = if(json.optBoolean("inscrito")) "SI" else "NO",
            fotoUrl = fullUrl
        )
    }
}