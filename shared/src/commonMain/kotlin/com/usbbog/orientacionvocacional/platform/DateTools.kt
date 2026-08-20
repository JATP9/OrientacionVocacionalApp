package com.usbbog.orientacionvocacional.platform

import kotlin.random.Random

internal data class CivilDate(
    val year: Int,
    val month: Int,
    val day: Int,
) : Comparable<CivilDate> {
    init {
        require(month in 1..12)
        require(day in 1..daysInMonth(year, month))
    }

    override fun compareTo(other: CivilDate): Int =
        compareValuesBy(this, other, CivilDate::year, CivilDate::month, CivilDate::day)

    fun minusYears(years: Int): CivilDate {
        val targetYear = year - years
        return CivilDate(targetYear, month, day.coerceAtMost(daysInMonth(targetYear, month)))
    }

    fun toIsoString(): String = "$year-${month.pad2()}-${day.pad2()}"

    fun toDisplayString(): String = "${day.pad2()}/${month.pad2()}/$year"

    companion object {
        fun parse(value: String): CivilDate? {
            val parts = value.take(10).split('-')
            if (parts.size != 3) return null
            val year = parts[0].toIntOrNull() ?: return null
            val month = parts[1].toIntOrNull() ?: return null
            val day = parts[2].toIntOrNull() ?: return null
            return runCatching { CivilDate(year, month, day) }.getOrNull()
        }
    }
}

internal fun todayUtc(nowMillis: Long = currentEpochMillis()): CivilDate =
    civilDateFromEpochMillis(nowMillis)

internal fun civilDateFromEpochMillis(epochMillis: Long): CivilDate {
    var z = floorDiv(epochMillis, MILLIS_PER_DAY) + 719_468L
    val era = floorDiv(z, 146_097L)
    val dayOfEra = z - era * 146_097L
    val yearOfEra = (
        dayOfEra - dayOfEra / 1_460L + dayOfEra / 36_524L - dayOfEra / 146_096L
        ) / 365L
    var year = yearOfEra + era * 400L
    val dayOfYear = dayOfEra - (365L * yearOfEra + yearOfEra / 4L - yearOfEra / 100L)
    val monthPrime = (5L * dayOfYear + 2L) / 153L
    val day = dayOfYear - (153L * monthPrime + 2L) / 5L + 1L
    val month = monthPrime + if (monthPrime < 10L) 3L else -9L
    year += if (month <= 2L) 1L else 0L
    return CivilDate(year.toInt(), month.toInt(), day.toInt())
}

internal fun CivilDate.toEpochMillis(): Long {
    var adjustedYear = year.toLong()
    if (month <= 2) adjustedYear -= 1L
    val era = floorDiv(adjustedYear, 400L)
    val yearOfEra = adjustedYear - era * 400L
    val monthPrime = month + if (month > 2) -3 else 9
    val dayOfYear = (153L * monthPrime + 2L) / 5L + day - 1L
    val dayOfEra = yearOfEra * 365L + yearOfEra / 4L - yearOfEra / 100L + dayOfYear
    return (era * 146_097L + dayOfEra - 719_468L) * MILLIS_PER_DAY
}

internal fun ageAt(
    birthDate: CivilDate,
    today: CivilDate,
): Int = today.year - birthDate.year - if (
    today.month < birthDate.month ||
    (today.month == birthDate.month && today.day < birthDate.day)
) {
    1
} else {
    0
}

internal fun formatServerDate(value: String?): String {
    if (value.isNullOrBlank()) return "Fecha no disponible"

    val date = CivilDate.parse(value) ?: return value.replace('T', ' ').take(16)
    val hour = value.getOrNull(11)?.let {
        value.substring(11, value.length.coerceAtMost(13)).toIntOrNull()
    }
    val minute = value.getOrNull(14)?.let {
        value.substring(14, value.length.coerceAtMost(16)).toIntOrNull()
    }
    if (hour == null || minute == null || hour !in 0..23 || minute !in 0..59) {
        return "${date.day} ${SPANISH_MONTHS[date.month - 1]} ${date.year}"
    }

    val period = if (hour < 12) "a. m." else "p. m."
    val displayHour = when (val hour12 = hour % 12) {
        0 -> 12
        else -> hour12
    }
    return "${date.day} ${SPANISH_MONTHS[date.month - 1]} ${date.year}, " +
        "$displayHour:${minute.pad2()} $period"
}

internal fun randomUuid(): String {
    val bytes = Random.Default.nextBytes(16)
    bytes[6] = ((bytes[6].toInt() and 0x0F) or 0x40).toByte()
    bytes[8] = ((bytes[8].toInt() and 0x3F) or 0x80).toByte()
    val hex = bytes.joinToString("") { byte ->
        (byte.toInt() and 0xFF).toString(16).padStart(2, '0')
    }
    return "${hex.substring(0, 8)}-${hex.substring(8, 12)}-${hex.substring(12, 16)}-" +
        "${hex.substring(16, 20)}-${hex.substring(20)}"
}

internal fun String.foldSpanish(): String = buildString(length) {
    this@foldSpanish.forEach { character ->
        append(
            when (character.lowercaseChar()) {
                'á', 'à', 'ä', 'â' -> 'a'
                'é', 'è', 'ë', 'ê' -> 'e'
                'í', 'ì', 'ï', 'î' -> 'i'
                'ó', 'ò', 'ö', 'ô' -> 'o'
                'ú', 'ù', 'ü', 'û' -> 'u'
                'ñ' -> 'n'
                else -> character.lowercaseChar()
            },
        )
    }
}

private fun floorDiv(value: Long, divisor: Long): Long {
    val quotient = value / divisor
    val remainder = value % divisor
    return if (remainder != 0L && (value < 0L) != (divisor < 0L)) quotient - 1L else quotient
}

private fun isLeapYear(year: Int): Boolean =
    year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    2 -> if (isLeapYear(year)) 29 else 28
    4, 6, 9, 11 -> 30
    else -> 31
}

private fun Int.pad2(): String = toString().padStart(2, '0')

private const val MILLIS_PER_DAY = 86_400_000L
private val SPANISH_MONTHS = listOf(
    "ene", "feb", "mar", "abr", "may", "jun",
    "jul", "ago", "sept", "oct", "nov", "dic",
)
