package com.xinlei.frontend.linkoria.app.core.network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.Instant

/**
 * Custom deserializer de Gson para convertir timestamps ISO-8601 a Instant
 *
 * Maneja formatos como:
 * - "2024-04-30T10:30:45.123Z"
 * - "2024-04-30T10:30:45Z"
 * - "2024-04-30T10:30:45.123456Z"
 */
class InstantDeserializer : JsonDeserializer<Instant> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Instant {
        return try {
            Instant.parse(json.asString)
        } catch (e: Exception) {
            throw IllegalArgumentException("No se puede parsear el Instant: ${json.asString}", e)
        }
    }
}