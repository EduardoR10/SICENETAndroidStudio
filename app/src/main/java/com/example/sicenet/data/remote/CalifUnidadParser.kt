package com.example.sicenet.data.remote

import com.example.sicenet.data.local.entity.CalifUnidadEntity
import org.json.JSONArray

object CalifUnidadParser {
    fun parse(jsonString: String): List<CalifUnidadEntity> {
        val lista = mutableListOf<CalifUnidadEntity>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                lista.add(
                    CalifUnidadEntity(
                        materia = item.optString("Materia", ""),
                        grupo = item.optString("Grupo", ""),
                        unidadesActivas = item.optString("UnidadesActivas", ""),
                        c1 = if (item.isNull("C1")) null else item.getString("C1"),
                        c2 = if (item.isNull("C2")) null else item.getString("C2"),
                        c3 = if (item.isNull("C3")) null else item.getString("C3"),
                        c4 = if (item.isNull("C4")) null else item.getString("C4"),
                        c5 = if (item.isNull("C5")) null else item.getString("C5"),
                        c6 = if (item.isNull("C6")) null else item.getString("C6"),
                        c7 = if (item.isNull("C7")) null else item.getString("C7"),
                        c8 = if (item.isNull("C8")) null else item.getString("C8"),
                        c9 = if (item.isNull("C9")) null else item.getString("C9"),
                        c10 = if (item.isNull("C10")) null else item.getString("C10"),
                        c11 = if (item.isNull("C11")) null else item.getString("C11"),
                        c12 = if (item.isNull("C12")) null else item.getString("C12"),
                        c13 = if (item.isNull("C13")) null else item.getString("C13"),
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