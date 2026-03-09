package com.example.sicenet.data.remote

import com.example.sicenet.data.local.entity.CalifFinalEntity
import org.json.JSONArray

object CalifFinalParser {
    fun parse(jsonString: String): List<CalifFinalEntity> {
        val lista = mutableListOf<CalifFinalEntity>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                lista.add(
                    CalifFinalEntity(
                        materia = item.optString("materia", ""),
                        grupo = item.optString("grupo", ""),
                        calif = item.optInt("calif", 0),
                        acreditacion = item.optString("acred", ""),
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