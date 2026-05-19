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