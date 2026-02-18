package com.example.sicenet.data

import com.example.sicenet.data.remote.SoapBodyUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SicenetRepository {

    //Se desactivan las dedirecciones automaticas para manejar el "Cookie Check" de ASP.NET manualmente
    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .followRedirects(false)
            .followSslRedirects(false)
            .addInterceptor(logging)
            .build()
    }

    private val BASE_URL = "https://sicenet.surguanajuato.tecnm.mx/ws/wsalumnos.asmx"
    private val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()

    suspend fun login(matricula: String, contrasenia: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                //Preparar la petición
                val soapBody = SoapBodyUtils.getLoginBody(matricula, contrasenia)

                val requestBuilder = Request.Builder()
                    .url(BASE_URL)
                    .post(soapBody.toRequestBody(MEDIA_TYPE_XML))
                    .addHeader("User-Agent", "Mozilla/5.0") //navegador
                    .addHeader("SOAPAction", "\"http://tempuri.org/accesoLogin\"")

                //Ejecutar petición
                var response = client.newCall(requestBuilder.build()).execute()

                //Si el servidor responde 302/307, es que quiere checar cookies.
                if (response.code == 302 || response.code == 307 || response.header("Location")?.contains("AspxAutoDetect") == true) {

                    println("DETECTADA REDIRECCIÓN DE COOKIES. Reintentando...")

                    // 1. Robar la cookie que nos dio el servidor
                    val cookies = response.headers("Set-Cookie")
                    if (cookies.isNotEmpty()) {
                        val cookieRaw = cookies[0].split(";")[0] //Tomamos solo la parte de la cookie
                        SessionManager.sessionCookie = cookieRaw
                    }
                    response.close()

                    //Reenviar el MISMO POST, pero ahora con la cookie pegada
                    val retryRequest = requestBuilder
                        .addHeader("Cookie", SessionManager.sessionCookie ?: "")
                        .build()

                    response = client.newCall(retryRequest).execute()
                }

                val responseBody = response.body?.string() ?: ""

                //Analizar la respuesta final
                if (response.isSuccessful) {
                    if (responseBody.contains("<html", ignoreCase = true)) {
                        return@withContext Result.failure(Exception("Error: El servidor sigue devolviendo HTML. Verifica matrícula/pass."))
                    }

                    // Guardar cookie si no la teníamos
                    val cookies = response.headers("Set-Cookie")
                    if (cookies.isNotEmpty()) {
                        SessionManager.sessionCookie = cookies[0].split(";")[0]
                    }

                    //Validar éxito en el XML
                    //Buscamos cualquier respuesta que no sea "false" o vacía
                    val resultado = extractResultWithRegex(responseBody, "accesoLoginResult")
                    val esValido = resultado.length > 5 && !resultado.contains("false", ignoreCase = true) // (Respuesta servidor: $resultado)

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
                    val contenido = extractResultWithRegex(responseBody, "getAlumnoAcademicoWithLineamientoResult")
                    Result.success(contenido)
                } else {
                    Result.failure(Exception("Error al obtener perfil: ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun extractResultWithRegex(xml: String, tagName: String): String {
        try {
            val pattern = "<$tagName[^>]*>(.*?)</$tagName>".toRegex(RegexOption.DOT_MATCHES_ALL)
            val match = pattern.find(xml)
            return match?.groups?.get(1)?.value ?: "Sin datos"
        } catch (e: Exception) {
            return "Error al leer XML"
        }
    }
}