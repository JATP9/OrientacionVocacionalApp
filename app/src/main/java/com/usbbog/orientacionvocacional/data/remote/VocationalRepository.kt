package com.usbbog.orientacionvocacional.data.remote

import com.usbbog.orientacionvocacional.data.session.SessionStore
import org.json.JSONArray
import org.json.JSONObject

object VocationalRepository {

    suspend fun login(
        username: String,
        password: String,
        remember: Boolean,
    ): LoginResponseDto {
        val response = ApiHttpClient.post(
            path = "/api/v1/auth/login",
            body = JSONObject()
                .put("username", username)
                .put("password", password),
            authenticated = false,
        ).asJsonObject()

        val login = LoginResponseDto(
            token = response.requiredString("token"),
            expiresIn = response.optLong("expiresIn"),
            username = response.requiredString("username"),
            role = response.requiredString("rol"),
        )

        SessionStore.save(
            token = login.token,
            username = login.username,
            role = login.role,
            remember = remember,
        )
        return login
    }

    suspend fun register(payload: RegistrationPayload): UserResponseDto {
        val response = ApiHttpClient.post(
            path = "/api/v1/auth/register",
            body = JSONObject()
                .putNullable("idPrograma", payload.programId)
                .put("nombre", payload.firstName)
                .put("apellidos", payload.lastName)
                .put("documento", payload.document)
                .put("correo", payload.email)
                .put("nombreUsuario", payload.username)
                .putNullable("telefono", payload.phone)
                .putNullable("fechaNacimiento", payload.birthDate)
                .putNullable("genero", payload.gender)
                .putNullable("generoOtro", payload.genderOther)
                .putNullable("departamento", payload.departmentId)
                .putNullable("municipio", payload.municipalityId)
                .putNullable("semestre", payload.semester)
                .put("contrasena", payload.password),
            authenticated = false,
        )
        return parseUser(response.asJsonObject())
    }

    suspend fun forgotPassword(email: String): String {
        val response = ApiHttpClient.post(
            path = "/api/v1/auth/forgot-password",
            body = JSONObject().put("correo", email),
            authenticated = false,
        ).asJsonObject()
        return response.optString("message")
            .ifBlank { "Si el correo está registrado, recibirás un enlace de recuperación." }
    }

    suspend fun resetPassword(token: String, newPassword: String): String {
        val response = ApiHttpClient.post(
            path = "/api/v1/auth/reset-password",
            body = JSONObject()
                .put("token", token)
                .put("nuevaContrasena", newPassword),
            authenticated = false,
        ).asJsonObject()
        return response.optString("message")
            .ifBlank { "Tu contraseña fue restablecida correctamente." }
    }

    suspend fun departments(): List<DepartmentDto> =
        ApiHttpClient.get(
            path = "/api/v1/departamentos",
            authenticated = false,
        ).asJsonArray().objects().map { item ->
            DepartmentDto(
                id = item.requiredString("idDepartamento"),
                name = item.requiredString("nombreDepartamento"),
            )
        }

    suspend fun municipalities(departmentId: String): List<MunicipalityDto> =
        ApiHttpClient.get(
            path = "/api/v1/departamentos/${departmentId.urlPathSegment()}/municipios",
            authenticated = false,
        ).asJsonArray().objects().map { item ->
            MunicipalityDto(
                id = item.requiredString("idMunicipio"),
                name = item.requiredString("nombreMunicipio"),
                departmentId = item.requiredString("idDepartamento"),
            )
        }

    suspend fun programs(): List<ProgramDto> =
        ApiHttpClient.get(
            path = "/api/v1/catalogos/programas",
            authenticated = false,
        ).asJsonArray().objects().flatMap { area ->
            val areaName = area.requiredString("nombreArea")
            area.optJSONArray("programas")?.objects().orEmpty().map { program ->
                ProgramDto(
                    id = program.requiredLong("id"),
                    name = program.requiredString("nombrePrograma"),
                    areaName = areaName,
                    url = program.nullableString("urlPrograma"),
                )
            }
        }

    suspend fun profile(): UserResponseDto =
        parseUser(ApiHttpClient.get("/api/v1/usuarios/me").asJsonObject())

    suspend fun updateProfile(payload: ProfileUpdatePayload): UserResponseDto {
        val response = ApiHttpClient.put(
            path = "/api/v1/usuarios/me/perfil",
            body = JSONObject()
                .putNullable("idPrograma", payload.programId)
                .put("nombre", payload.firstName)
                .put("apellidos", payload.lastName)
                .putNullable("telefono", payload.phone)
                .putNullable("genero", payload.gender)
                .putNullable("generoOtro", payload.genderOther)
                .putNullable("departamento", payload.departmentId)
                .putNullable("municipio", payload.municipalityId)
                .putNullable("semestre", payload.semester),
        )
        return parseUser(response.asJsonObject())
    }

    suspend fun changePassword(currentPassword: String, newPassword: String) {
        ApiHttpClient.post(
            path = "/api/v1/usuarios/me/cambio-password",
            body = JSONObject()
                .put("passwordActual", currentPassword)
                .put("passwordNueva", newPassword),
        )
    }

    suspend fun deleteAccount() {
        ApiHttpClient.delete("/api/v1/usuarios/me")
    }

    suspend fun questions(): List<QuestionResponseDto> =
        ApiHttpClient.get("/api/v1/preguntas/para-prueba")
            .asJsonArray()
            .objects()
            .map { item ->
                QuestionResponseDto(
                    id = item.requiredLong("id"),
                    code = item.requiredString("codigo"),
                    statement = item.requiredString("enunciado"),
                    programId = item.requiredLong("idPrograma"),
                    programName = item.requiredString("nombrePrograma"),
                    areaId = item.requiredLong("idArea"),
                    areaName = item.requiredString("nombreArea"),
                )
            }

    suspend fun submitTest(
        elapsedSeconds: Int,
        version: String,
        answers: List<TestAnswerDto>,
    ): TestResultResponseDto {
        val answersJson = JSONArray().apply {
            answers.forEach { answer ->
                put(
                    JSONObject()
                        .put("preguntaId", answer.questionId)
                        .put("codigoPregunta", answer.questionCode)
                        .put("valor", answer.value),
                )
            }
        }

        val response = ApiHttpClient.post(
            path = "/api/v1/pruebas",
            body = JSONObject()
                .put("tiempoInvertido", elapsedSeconds)
                .put("versionPrueba", version)
                .put("satisfaccion", JSONObject.NULL)
                .put("respuestas", answersJson),
        ).asJsonObject()

        return parseTestResult(response)
    }

    suspend fun myTests(): List<TestHistoryDto> =
        ApiHttpClient.get("/api/v1/pruebas/mis-pruebas")
            .asJsonArray()
            .objects()
            .mapNotNull { test ->
                val id = test.nullableLong("id") ?: return@mapNotNull null
                TestHistoryDto(
                    id = id,
                    date = test.nullableString("fecha"),
                    elapsedSeconds = test.nullableInt("tiempoInvertido"),
                    version = test.nullableString("versionPrueba"),
                    active = test.optBoolean("activo", true),
                )
            }

    suspend fun testResult(testId: Long): TestResultResponseDto =
        parseTestResult(
            ApiHttpClient.get("/api/v1/pruebas/$testId/resultado").asJsonObject(),
        )

    fun logout() = SessionStore.clear()

    private fun parseUser(json: JSONObject): UserResponseDto = UserResponseDto(
        id = json.nullableLong("id"),
        roleId = json.requiredLong("idRol"),
        programId = json.nullableLong("idPrograma"),
        firstName = json.requiredString("nombre"),
        lastName = json.requiredString("apellidos"),
        document = json.requiredString("documento"),
        email = json.requiredString("correo"),
        username = json.nullableString("nombreUsuario"),
        phone = json.nullableString("telefono"),
        birthDate = json.nullableString("fechaNacimiento"),
        gender = json.nullableString("genero"),
        genderOther = json.nullableString("generoOtro"),
        departmentId = json.nullableString("departamento"),
        municipalityId = json.nullableString("municipio"),
        semester = json.nullableInt("semestre"),
        active = json.optBoolean("estado", true),
    )

    private fun parseTestResult(json: JSONObject): TestResultResponseDto =
        TestResultResponseDto(
            testId = json.requiredLong("idPrueba"),
            date = json.nullableString("fecha"),
            predominantAreaId = json.requiredLong("idAreaPredominante"),
            predominantAreaName = json.requiredString("nombreAreaPredominante"),
            profile = json.nullableString("perfil"),
            areaDescription = json.nullableString("descripcionArea"),
            areaAffinities = json.optJSONArray("afinidadPorArea")
                ?.objects()
                .orEmpty()
                .map { area ->
                    AreaAffinityDto(
                        areaId = area.requiredLong("idArea"),
                        areaName = area.requiredString("nombreArea"),
                        affinity = area.optInt("valorAfinidad"),
                        profile = area.nullableString("perfil"),
                        description = area.nullableString("descripcionArea"),
                        logoPath = area.nullableString("pathLogo"),
                        pachoPath = area.nullableString("pachoPath"),
                    )
                },
            recommendedPrograms = json.optJSONArray("programasRecomendados")
                ?.objects()
                .orEmpty()
                .map { program ->
                    ProgramAffinityDto(
                        programId = program.requiredLong("idPrograma"),
                        programName = program.requiredString("nombrePrograma"),
                        affinity = program.optInt("valorAfinidad"),
                        description = program.nullableString("descripcionPrograma"),
                        url = program.nullableString("urlPrograma"),
                        logoPath = program.nullableString("pathLogo"),
                        areaName = program.nullableString("nombreArea"),
                    )
                },
            reportName = json.requiredString("nombreReporte"),
            reportUrl = json.nullableString("url"),
        )
}

private fun String.asJsonObject(): JSONObject =
    runCatching { JSONObject(this) }
        .getOrElse { throw ApiException(null, "El backend devolvió una respuesta inválida.") }

private fun String.asJsonArray(): JSONArray =
    runCatching { JSONArray(this) }
        .getOrElse { throw ApiException(null, "El backend devolvió una respuesta inválida.") }

private fun JSONArray.objects(): List<JSONObject> =
    runCatching {
        (0 until length()).map { index -> getJSONObject(index) }
    }.getOrElse {
        throw ApiException(null, "El backend devolvió una lista con formato inválido.")
    }

private fun JSONObject.requiredString(name: String): String =
    nullableString(name)?.takeIf(String::isNotBlank)
        ?: throw ApiException(null, "La respuesta del backend no contiene '$name'.")

private fun JSONObject.requiredLong(name: String): Long =
    nullableLong(name)
        ?: throw ApiException(null, "La respuesta del backend no contiene '$name'.")

private fun JSONObject.nullableString(name: String): String? =
    if (!has(name) || isNull(name)) null else optString(name).takeIf { it.isNotBlank() }

private fun JSONObject.nullableLong(name: String): Long? =
    if (!has(name) || isNull(name)) null else optLong(name)

private fun JSONObject.nullableInt(name: String): Int? =
    if (!has(name) || isNull(name)) null else optInt(name)

private fun JSONObject.putNullable(name: String, value: Any?): JSONObject =
    put(name, value ?: JSONObject.NULL)

private fun String.urlPathSegment(): String =
    java.net.URLEncoder.encode(this, Charsets.UTF_8.name()).replace("+", "%20")
