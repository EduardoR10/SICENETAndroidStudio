package com.example.sicenet.data.remote

import com.example.sicenet.data.local.entity.KardexEntity
import org.json.JSONObject

object KardexParser {
    fun parse(jsonString: String): List<KardexEntity> {
        val lista = mutableListOf<KardexEntity>()
        try {
            val root = JSONObject(jsonString)
            val promedioObj = root.getJSONObject("Promedio")
            val promedioGral = promedioObj.optDouble("PromedioGral", 0.0)
            val avance = promedioObj.optDouble("AvanceCdts", 0.0)

            val array = root.getJSONArray("lstKardex")
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                lista.add(KardexEntity(
                    clvMat = item.optString("ClvMat"),
                    clvOfiMat = item.optString("ClvOfiMat"),
                    materia = item.optString("Materia"),
                    cdts = item.optInt("Cdts"),
                    calif = item.optInt("Calif"),
                    acreditacion = item.optString("Acred"),
                    periodo = "${item.optString("P1")} ${item.optString("A1")}",
                    promedioGral = promedioGral,
                    avanceCdts = avance
                ))
            }
        } catch (e: Exception) { e.printStackTrace() }
        return lista
    }
}