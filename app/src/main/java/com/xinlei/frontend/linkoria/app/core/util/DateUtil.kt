package com.xinlei.frontend.linkoria.app.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val LOCALE_ES = Locale("es")

// "dd/MM/yy" → 2-digit year, e.g. 04/06/25
private val DATE_FORMATTER_DD_MM_YY: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd/MM/yy")
    .withZone(ZoneId.systemDefault())

// Día de la semana en español, e.g. "lunes"
private val DATE_FORMATTER_DAY_ES: DateTimeFormatter = DateTimeFormatter
    .ofPattern("EEEE", LOCALE_ES)
    .withZone(ZoneId.systemDefault())

private val TIME_FORMATTER_HH_MM: DateTimeFormatter = DateTimeFormatter
    .ofPattern("HH:mm")
    .withZone(ZoneId.systemDefault())

/**
 * Devuelve una representación legible de la fecha:
 *  - Hoy o futuro  → "Hoy"
 *  - Últimos 7 días → nombre del día en español, e.g. "Lunes"
 *  - Más antiguo   → dd/MM/yy, e.g. "04/06/25"
 */
fun Instant.smartDate(): String {
    val systemZone = ZoneId.systemDefault()
    val currentDate = ZonedDateTime.now(systemZone).toLocalDate()
    val targetDate = this.atZone(systemZone).toLocalDate()

    val daysDifference = ChronoUnit.DAYS.between(targetDate, currentDate)

    return when {
        // FIX #1: cualquier valor <= 0 (hoy o futuro) → "Hoy"
        daysDifference <= 0 -> "Hoy"
        daysDifference < 7 -> DATE_FORMATTER_DAY_ES.format(this)
            // FIX #7: usar LOCALE_ES en lugar de Locale.getDefault()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(LOCALE_ES) else it.toString() }
        else -> DATE_FORMATTER_DD_MM_YY.format(this)
    }
}

fun Instant.smartTime(): String {
    return TIME_FORMATTER_HH_MM.format(this)
}

/**
 * Returns the elapsed time since this instant in a compact human-readable format:
 * 5s · 23m · 4h · 3d · 2w · 1yr / 3yrs
 */
fun Instant.smartElapsed(): String {
    val systemZone = ZoneId.systemDefault()
    val now = Instant.now()

    val seconds = ChronoUnit.SECONDS.between(this, now)
    if (seconds < 0) return "0s" // Future date or clock mismatch

    if (seconds < 60) {
        return "${seconds}s"
    }

    val minutes = ChronoUnit.MINUTES.between(this, now)
    if (minutes < 60) {
        return "${minutes}m"
    }

    val hours = ChronoUnit.HOURS.between(this, now)
    if (hours < 24) {
        return "${hours}h"
    }

    val days = ChronoUnit.DAYS.between(this, now)
    if (days < 7) {
        return "${days}d"
    }

    // ChronoUnit.WEEKS is not supported on Instant in Android
    // (UnsupportedTemporalTypeException). Derived from already-computed days instead.
    val weeks = days / 7
    if (weeks < 52) {
        return "${weeks}w"
    }

    // Using LocalDate to correctly account for DST changes when computing full years.
    val years = ChronoUnit.YEARS.between(
        this.atZone(systemZone).toLocalDate(),
        ZonedDateTime.now(systemZone).toLocalDate()
    )
    return "$years${if (years == 1L) "yr" else "yrs"}"
}