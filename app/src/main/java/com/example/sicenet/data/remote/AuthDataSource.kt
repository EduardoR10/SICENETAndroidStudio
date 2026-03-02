package com.example.sicenet.data.remote

import com.example.sicenet.data.SessionManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthDataSource {

    private val client = SicenetClient.instance
    private val BASE_URL = "https://sicenet.surguanajuato.tecnm.mx/ws/wsalumnos.asmx"
    private val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()

    suspend fun login(matricula: String, contrasenia: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val soapBody = SoapBodyUtils.getLoginBody(matricula, contrasenia)

                val requestBuilder = Request.Builder()
                    .url(BASE_URL)
                    .post(soapBody.toRequestBody(MEDIA_TYPE_XML))
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .addHeader("SOAPAction", "\"http://tempuri.org/accesoLogin\"")

                var response = client.newCall(requestBuilder.build()).execute()

                if (response.code == 302 || response.code == 307 || response.header("Location")?.contains("AspxAutoDetect") == true) {
                    val cookies = response.headers("Set-Cookie")
                    if (cookies.isNotEmpty()) {
                        val cookieRaw = cookies[0].split(";")[0]
                        SessionManager.sessionCookie = cookieRaw
                    }
                    response.close()

                    val retryRequest = requestBuilder
                        .addHeader("Cookie", SessionManager.sessionCookie ?: "")
                        .build()

                    response = client.newCall(retryRequest).execute()
                }

                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    if (responseBody.contains("<html", ignoreCase = true)) {
                        return@withContext Result.failure(Exception("Error: El servidor sigue devolviendo HTML. Verifica matrícula/pass."))
                    }

                    val cookies = response.headers("Set-Cookie")
                    if (cookies.isNotEmpty()) {
                        SessionManager.sessionCookie = cookies[0].split(";")[0]
                    }

                    val resultado = XmlParserUtils.extractResultWithRegex(responseBody, "accesoLoginResult")
                    val esValido = resultado.length > 5 && !resultado.contains("false", ignoreCase = true)

                    if (esValido) Result.success(true) else Result.failure(Exception("Credenciales incorrectas"))
                } else {
                    Result.failure(Exception("Error HTTP: ${response.code}"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}