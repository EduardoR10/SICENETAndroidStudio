package com.example.sicenet.data.remote

object XmlParserUtils {
    fun extractResultWithRegex(xml: String, tagName: String): String {
        try {
            val pattern = "<$tagName[^>]*>(.*?)</$tagName>".toRegex(RegexOption.DOT_MATCHES_ALL)
            val match = pattern.find(xml)
            return match?.groups?.get(1)?.value ?: "Sin datos"
        } catch (e: Exception) {
            return "Error al leer XML"
        }
    }
}