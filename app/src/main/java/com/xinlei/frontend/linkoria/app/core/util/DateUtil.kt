package com.xinlei.frontend.linkoria.app.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val DATE_FORMATTER_DD_MM_YY: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd/MM/yy")
    .withZone(ZoneId.systemDefault())

private val DATE_FORMATTER_DAY_ES: DateTimeFormatter = DateTimeFormatter
    .ofPattern("EEEE", Locale("es"))
    .withZone(ZoneId.systemDefault())

private val TIME_FORMATTER_HH_MM: DateTimeFormatter = DateTimeFormatter
    .ofPattern("HH:mm")
    .withZone(ZoneId.systemDefault())

fun Instant.smartDate(): String {
    val systemZone = ZoneId.systemDefault()
    val currentDate = ZonedDateTime.now(systemZone).toLocalDate()
    val targetDate = this.atZone(systemZone).toLocalDate()

    val daysDifference = ChronoUnit.DAYS.between(targetDate, currentDate)

    return when {
        daysDifference < 1 -> "Hoy"
        daysDifference < 7 -> DATE_FORMATTER_DAY_ES.format(this)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        else -> DATE_FORMATTER_DD_MM_YY.format(this)
    }
}

fun Instant.smartTime(): String {
    return TIME_FORMATTER_HH_MM.format(this)
}

fun Instant.smartElapsed(): String {
    val now = Instant.now()
    val systemZone = ZoneId.systemDefault()

    val seconds = ChronoUnit.SECONDS.between(this, now)
    if (seconds < 0) return "0 s" // Prevent future date offsets or clock mismatches

    if (seconds < 60) {
        return "$seconds s"
    }

    val minutes = ChronoUnit.MINUTES.between(this, now)
    if (minutes < 60) {
        return "$minutes m"
    }

    val hours = ChronoUnit.HOURS.between(this, now)
    if (hours < 24) {
        return "$hours h"
    }

    val days = ChronoUnit.DAYS.between(this, now)
    if (days < 7) {
        return "$days d"
    }

    val weeks = ChronoUnit.WEEKS.between(this, now)
    if (weeks < 52) {
        return "$weeks wk"
    }

    val years = ChronoUnit.YEARS.between(
        this.atZone(systemZone).toLocalDate(),
        ZonedDateTime.now(systemZone).toLocalDate()
    )
    val yearLabel = if (years > 1) "yrs" else "yr"
    return "$years $yearLabel"
}