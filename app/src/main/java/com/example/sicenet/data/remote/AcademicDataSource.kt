package com.example.sicenet.data.remote

import com.example.sicenet.data.SessionManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AcademicDataSource {

    private val client = SicenetClient.instance
    private val BASE_URL = "https://sicenet.surguanajuato.tecnm.mx/ws/wsalumnos.asmx"
    private val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()

    suspend fun getAlumnoAcademico(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!SessionManager.isLoggedIn()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val soapBody = SoapBodyUtils.getPerfilBody()

                val request = Request.Builder()
                    .url(BASE_URL)
                    .post(soapBody.toRequestBody(MEDIA_TYPE_XML))
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .addHeader("SOAPAction", "\"http://tempuri.org/getAlumnoAcademicoWithLineamiento\"")
                    .addHeader("Cookie", SessionManager.sessionCookie ?: "")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val contenido = XmlParserUtils.extractResultWithRegex(responseBody, "getAlumnoAcademicoWithLineamientoResult")
                    Result.success(contenido)
                } else {
                    Result.failure(Exception("Error al obtener perfil: ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    suspend fun getCargaAcademica(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!SessionManager.isLoggedIn()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val soapBody = SoapBodyUtils.getCargaAcademicaBody()

                val request = Request.Builder()
                    .url(BASE_URL)
                    .post(soapBody.toRequestBody(MEDIA_TYPE_XML))
                    .addHeader("User-Agent", "Mozilla/5.0")
                    // IMPORTANTE: El SOAPAction específico que me diste
                    .addHeader("SOAPAction", "\"http://tempuri.org/getCargaAcademicaByAlumno\"")
                    .addHeader("Cookie", SessionManager.sessionCookie ?: "")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    // Extraemos lo que hay dentro de la etiqueta Result
                    val contenido = XmlParserUtils.extractResultWithRegex(responseBody, "getCargaAcademicaByAlumnoResult")
                    Result.success(contenido)
                } else {
                    Result.failure(Exception("Error al obtener carga académica: ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}