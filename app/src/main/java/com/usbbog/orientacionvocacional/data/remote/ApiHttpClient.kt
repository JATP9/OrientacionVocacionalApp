package com.usbbog.orientacionvocacional.data.remote

import com.usbbog.orientacionvocacional.BuildConfig
import com.usbbog.orientacionvocacional.data.session.SessionStore
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ApiException(
    val statusCode: Int?,
    override val message: String,
) : Exception(message)

object ApiHttpClient {

    private const val CONNECT_TIMEOUT_MILLIS = 15_000
    private const val READ_TIMEOUT_MILLIS = 30_000

    suspend fun get(
        path: String,
        authenticated: Boolean = true,
    ): String = request("GET", path, authenticated = authenticated)

    suspend fun post(
        path: String,
        body: JSONObject,
        authenticated: Boolean = true,
    ): String = request("POST", path, body, authenticated)

    suspend fun put(
        path: String,
        body: JSONObject,
        authenticated: Boolean = true,
    ): String = request("PUT", path, body, authenticated)

    suspend fun delete(
        path: String,
        authenticated: Boolean = true,
    ): String = request("DELETE", path, authenticated = authenticated)

    private suspend fun request(
        method: String,
        path: String,
        body: JSONObject? = null,
        authenticated: Boolean,
    ): String = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null

        try {
            val activeConnection = (URL(resolve(path)).openConnection() as HttpURLConnection).apply {
                requestMethod = method
                connectTimeout = CONNECT_TIMEOUT_MILLIS
                readTimeout = READ_TIMEOUT_MILLIS
                useCaches = false
                doInput = true
                setRequestProperty("Accept", "application/json")

                if (authenticated) {
                    val token = SessionStore.token
                        ?: throw ApiException(
                            401,
                            "Tu sesión no está disponible. Inicia sesión nuevamente.",
                        )
                    setRequestProperty("Authorization", "Bearer $token")
                }

                if (body != null) {
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                }
            }
            connection = activeConnection

            if (body != null) {
                activeConnection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                    writer.write(body.toString())
                }
            }

            val status = activeConnection.responseCode
            val stream = if (status in 200..299) {
                activeConnection.inputStream
            } else {
                activeConnection.errorStream
            }
            val responseBody = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()

            if (status !in 200..299) {
                if (status == HttpURLConnection.HTTP_UNAUTHORIZED && authenticated) {
                    SessionStore.clear()
                }
                throw ApiException(status, errorMessage(status, responseBody))
            }

            responseBody
        } catch (error: ApiException) {
            throw error
        } catch (_: SocketTimeoutException) {
            throw ApiException(
                statusCode = null,
                message = "El backend tardó demasiado en responder. Verifica que esté ejecutándose.",
            )
        } catch (_: IOException) {
            throw ApiException(
                statusCode = null,
                message = "No fue posible conectar con el backend. Revisa la URL configurada y la red.",
            )
        } finally {
            connection?.disconnect()
        }
    }

    private fun resolve(path: String): String =
        BuildConfig.API_BASE_URL + path.removePrefix("/")

    private fun errorMessage(status: Int, responseBody: String): String {
        val json = runCatching { JSONObject(responseBody) }.getOrNull()
        val message = json?.optString("message").orEmpty().trim()
        val error = json?.optString("error").orEmpty().trim()
        val preferred = when (message.uppercase()) {
            "BAD_REQUEST", "UNAUTHORIZED", "FORBIDDEN", "NOT_FOUND", "CONFLICT" -> error
            else -> message
        }

        if (preferred.isNotBlank()) return preferred

        return when (status) {
            400 -> "La solicitud enviada no es válida."
            401 -> "Las credenciales no son válidas o la sesión expiró."
            403 -> "No tienes permisos para realizar esta acción."
            404 -> "El recurso solicitado no fue encontrado."
            409 -> "La información entra en conflicto con un registro existente."
            else -> "El backend respondió con un error ($status)."
        }
    }
}
