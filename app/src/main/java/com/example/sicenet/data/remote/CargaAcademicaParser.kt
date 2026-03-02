package com.example.sicenet.data.remote

import com.example.sicenet.data.local.entity.CargaAcademicaEntity
import org.json.JSONArray

object CargaAcademicaParser {
    fun parse(jsonString: String): List<CargaAcademicaEntity> {
        val lista = mutableListOf<CargaAcademicaEntity>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                lista.add(
                    CargaAcademicaEntity(
                        materia = item.optString("Materia", ""),
                        docente = item.optString("Docente", ""),
                        clvOficial = item.optString("clvOficial", ""),
                        grupo = item.optString("Grupo", ""),
                        creditos = item.optInt("CreditosMateria", 0),
                        estadoMateria = item.optString("EstadoMateria", ""),
                        lunes = item.optString("Lunes", ""),
                        martes = item.optString("Martes", ""),
                        miercoles = item.optString("Miercoles", ""),
                        jueves = item.optString("Jueves", ""),
                        viernes = item.optString("Viernes", ""),
                        sabado = item.optString("Sabado", ""),
                        observaciones = item.optString("Observaciones", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lista
    }
}