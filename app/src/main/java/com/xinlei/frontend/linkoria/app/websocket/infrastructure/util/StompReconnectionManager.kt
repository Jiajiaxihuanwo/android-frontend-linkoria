package com.xinlei.frontend.linkoria.app.websocket.infrastructure.util

import kotlinx.coroutines.delay
import kotlin.math.min

/**
 * Gestor de reconexión automática con backoff exponencial.
 *
 * Configuración:
 * - Intento inicial: 1 segundo
 * - Multiplicador: 2x
 * - Máximo: 30 segundos
 * - Máximo de reintentos: 10
 */
class StompReconnectionManager (
    private val initialDelayMs: Long = 1000,
    private val maxDelayMs: Long = 30000,
    private val multiplier: Double = 2.0,
    private val maxRetries: Int = 10
) {
    @Volatile private var currentRetryCount = 0
    @Volatile private var currentDelayMs = initialDelayMs

    /**
     * Ejecuta una acción con reintentos automáticos
     */
    suspend fun <T> executeWithRetry(
        action: suspend () -> T
    ): Result<T> {
        currentRetryCount = 0
        currentDelayMs = initialDelayMs

        while (currentRetryCount <= maxRetries) {
            try {
                val result = action()
                onSuccess()
                return Result.success(result)
            } catch (e: Exception) {
                currentRetryCount++
                if (currentRetryCount > maxRetries) {
                    return Result.failure(e)
                }

                val nextDelay = calculateNextDelay()
                delay(nextDelay)
            }
        }

        return Result.failure(Exception("Max retries exceeded"))
    }

    private fun calculateNextDelay(): Long {
        val exponentialDelay = (initialDelayMs * Math.pow(multiplier, currentRetryCount.toDouble())).toLong()
        val delayWithJitter = exponentialDelay + (Math.random() * 1000).toLong() // Jitter de ±1s
        currentDelayMs = min(delayWithJitter, maxDelayMs)
        return currentDelayMs
    }

    private fun onSuccess() {
        currentRetryCount = 0
        currentDelayMs = initialDelayMs
    }

    fun reset() {
        currentRetryCount = 0
        currentDelayMs = initialDelayMs
    }
}