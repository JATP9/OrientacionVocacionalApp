package com.usbbog.orientacionvocacional.viewmodel

import com.usbbog.orientacionvocacional.platform.ageAt
import com.usbbog.orientacionvocacional.platform.civilDateFromEpochMillis
import com.usbbog.orientacionvocacional.platform.currentEpochMillis

internal const val USERNAME_MAX_LENGTH = 50
internal const val GENDER_OTHER_MAX_LENGTH = 100
internal const val PASSWORD_MIN_LENGTH = 8
internal const val PASSWORD_MAX_LENGTH = 128

private val emailRegex = Regex(
    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
)
private val phoneRegex = Regex("^\\+?[0-9 ]{7,16}$")
private val uppercaseRegex = Regex("[A-Z]")
private val lowercaseRegex = Regex("[a-z]")
private val digitRegex = Regex("\\d")
private val specialCharacterRegex = Regex("[^A-Za-z0-9]")

internal fun normalizeEmail(value: String): String = value.trim().lowercase()

internal fun isValidEmail(value: String): Boolean = emailRegex.matches(value.trim())

internal fun isValidPhone(value: String): Boolean = phoneRegex.matches(value.trim())

internal fun passwordRequirementText(): String =
    "La contraseña debe tener al menos $PASSWORD_MIN_LENGTH caracteres e incluir " +
        "una mayúscula, una minúscula, un número y un carácter especial."

internal fun passwordValidationError(password: String): String? = when {
    password.length < PASSWORD_MIN_LENGTH ->
        "La contraseña debe tener al menos $PASSWORD_MIN_LENGTH caracteres."

    password.length > PASSWORD_MAX_LENGTH ->
        "La contraseña no puede superar $PASSWORD_MAX_LENGTH caracteres."

    !uppercaseRegex.containsMatchIn(password) ->
        "La contraseña debe incluir al menos una letra mayúscula."

    !lowercaseRegex.containsMatchIn(password) ->
        "La contraseña debe incluir al menos una letra minúscula."

    !digitRegex.containsMatchIn(password) ->
        "La contraseña debe incluir al menos un número."

    !specialCharacterRegex.containsMatchIn(password) ->
        "La contraseña debe incluir al menos un carácter especial."

    else -> null
}

internal fun calculateAge(
    birthDateMillis: Long,
    nowMillis: Long = currentEpochMillis(),
): Int = ageAt(
    birthDate = civilDateFromEpochMillis(birthDateMillis),
    today = civilDateFromEpochMillis(nowMillis),
)

internal fun birthDateValidationError(
    birthDateMillis: Long?,
    nowMillis: Long = currentEpochMillis(),
): String? {
    if (birthDateMillis == null) return "Selecciona la fecha de nacimiento."

    val birthDate = civilDateFromEpochMillis(birthDateMillis)
    val today = civilDateFromEpochMillis(nowMillis)
    if (birthDate > today) return "La fecha de nacimiento no puede ser futura."

    val oldestAllowed = today.minusYears(120)
    if (birthDate < oldestAllowed) return "Selecciona una fecha de nacimiento válida."

    return if (calculateAge(birthDateMillis, nowMillis) < 18) {
        "Debes ser mayor de edad."
    } else {
        null
    }
}
