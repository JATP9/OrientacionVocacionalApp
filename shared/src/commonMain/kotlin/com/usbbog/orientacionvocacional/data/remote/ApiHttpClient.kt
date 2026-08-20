package com.usbbog.orientacionvocacional.data.remote

import com.usbbog.orientacionvocacional.data.session.SessionStore
import com.usbbog.orientacionvocacional.platform.AppEnvironment
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException

class ApiException(
    val statusCode: Int?,
    override val message: String,
) : Exception(message)

internal object ApiHttpClient {
    private const val CONNECT_TIMEOUT_MILLIS = 15_000L
    private const val READ_TIMEOUT_MILLIS = 30_000L

    private val client by lazy {
        HttpClient {
            install(HttpTimeout) {
                connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
                socketTimeoutMillis = READ_TIMEOUT_MILLIS
                requestTimeoutMillis = CONNECT_TIMEOUT_MILLIS + READ_TIMEOUT_MILLIS
            }
        }
    }

    internal suspend fun get(
        path: String,
        authenticated: Boolean = true,
    ): String = request(HttpMethod.Get, path, authenticated = authenticated)

    internal suspend fun post(
        path: String,
        body: JSONObject,
        authenticated: Boolean = true,
    ): String = request(HttpMethod.Post, path, body, authenticated)

    internal suspend fun put(
        path: String,
        body: JSONObject,
        authenticated: Boolean = true,
    ): String = request(HttpMethod.Put, path, body, authenticated)

    internal suspend fun delete(
        path: String,
        authenticated: Boolean = true,
    ): String = request(HttpMethod.Delete, path, authenticated = authenticated)

    private suspend fun request(
        method: HttpMethod,
        path: String,
        body: JSONObject? = null,
        authenticated: Boolean,
    ): String {
        try {
            val response = client.request(resolve(path)) {
                this.method = method
                accept(ContentType.Application.Json)

                if (authenticated) {
                    val token = SessionStore.token
                        ?: throw ApiException(
                            401,
                            "Tu sesión no está disponible. Inicia sesión nuevamente.",
                        )
                    header(HttpHeaders.Authorization, "Bearer $token")
                }

                if (body != null) {
                    contentType(ContentType.Application.Json)
                    setBody(body.toString())
                }
            }

            val status = response.status.value
            val responseBody = response.bodyAsText()
            if (status !in 200..299) {
                if (status == 401 && authenticated) SessionStore.clear()
                throw ApiException(status, errorMessage(status, responseBody))
            }
            return responseBody
        } catch (error: ApiException) {
            throw error
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            val isTimeout = error::class.simpleName.orEmpty().contains(
                other = "timeout",
                ignoreCase = true,
            )
            throw ApiException(
                statusCode = null,
                message = if (isTimeout) {
                    "El backend tardó demasiado en responder. Verifica que esté ejecutándose."
                } else {
                    "No fue posible conectar con el backend. Revisa la URL configurada y la red."
                },
            )
        }
    }

    private fun resolve(path: String): String =
        AppEnvironment.apiBaseUrl + path.removePrefix("/")

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
