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
                if (!SessionManager.isLoggedIn()) return@withContext Result.failure(Exception("No hay sesión activa"))
                val request = Request.Builder().url(BASE_URL).post(SoapBodyUtils.getPerfilBody().toRequestBody(MEDIA_TYPE_XML))
                    .addHeader("User-Agent", "Mozilla/5.0").addHeader("SOAPAction", "\"http://tempuri.org/getAlumnoAcademicoWithLineamiento\"")
                    .addHeader("Cookie", SessionManager.sessionCookie ?: "").build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) Result.success(XmlParserUtils.extractResultWithRegex(response.body?.string() ?: "", "getAlumnoAcademicoWithLineamientoResult"))
                else Result.failure(Exception("Error HTTP: ${response.code}"))
            } catch (e: Exception) { Result.failure(e) }
        }
    }

    suspend fun getCargaAcademica(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!SessionManager.isLoggedIn()) return@withContext Result.failure(Exception("No hay sesión activa"))
                val request = Request.Builder().url(BASE_URL).post(SoapBodyUtils.getCargaAcademicaBody().toRequestBody(MEDIA_TYPE_XML))
                    .addHeader("User-Agent", "Mozilla/5.0").addHeader("SOAPAction", "\"http://tempuri.org/getCargaAcademicaByAlumno\"")
                    .addHeader("Cookie", SessionManager.sessionCookie ?: "").build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) Result.success(XmlParserUtils.extractResultWithRegex(response.body?.string() ?: "", "getCargaAcademicaByAlumnoResult"))
                else Result.failure(Exception("Error HTTP: ${response.code}"))
            } catch (e: Exception) { Result.failure(e) }
        }
    }

    suspend fun getKardex(lineamiento: Int = 3): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!SessionManager.isLoggedIn()) return@withContext Result.failure(Exception("No hay sesión activa"))
                val request = Request.Builder().url(BASE_URL).post(SoapBodyUtils.getKardexBody(lineamiento).toRequestBody(MEDIA_TYPE_XML))
                    .addHeader("User-Agent", "Mozilla/5.0").addHeader("SOAPAction", "\"http://tempuri.org/getAllKardexConPromedioByAlumno\"")
                    .addHeader("Cookie", SessionManager.sessionCookie ?: "").build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) Result.success(XmlParserUtils.extractResultWithRegex(response.body?.string() ?: "", "getAllKardexConPromedioByAlumnoResult"))
                else Result.failure(Exception("Error HTTP: ${response.code}"))
            } catch (e: Exception) { Result.failure(e) }
        }
    }

    suspend fun getCalifUnidades(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!SessionManager.isLoggedIn()) return@withContext Result.failure(Exception("No hay sesión activa"))
                val request = Request.Builder().url(BASE_URL).post(SoapBodyUtils.getCalifUnidadesBody().toRequestBody(MEDIA_TYPE_XML))
                    .addHeader("User-Agent", "Mozilla/5.0").addHeader("SOAPAction", "\"http://tempuri.org/getCalifUnidadesByAlumno\"")
                    .addHeader("Cookie", SessionManager.sessionCookie ?: "").build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) Result.success(XmlParserUtils.extractResultWithRegex(response.body?.string() ?: "", "getCalifUnidadesByAlumnoResult"))
                else Result.failure(Exception("Error HTTP: ${response.code}"))
            } catch (e: Exception) { Result.failure(e) }
        }
    }

    suspend fun getCalifFinales(modEducativo: Int = 1): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!SessionManager.isLoggedIn()) return@withContext Result.failure(Exception("No hay sesión activa"))
                val request = Request.Builder().url(BASE_URL).post(SoapBodyUtils.getCalifFinalesBody(modEducativo).toRequestBody(MEDIA_TYPE_XML))
                    .addHeader("User-Agent", "Mozilla/5.0").addHeader("SOAPAction", "\"http://tempuri.org/getAllCalifFinalByAlumnos\"")
                    .addHeader("Cookie", SessionManager.sessionCookie ?: "").build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) Result.success(XmlParserUtils.extractResultWithRegex(response.body?.string() ?: "", "getAllCalifFinalByAlumnosResult"))
                else Result.failure(Exception("Error HTTP: ${response.code}"))
            } catch (e: Exception) { Result.failure(e) }
        }
    }
}