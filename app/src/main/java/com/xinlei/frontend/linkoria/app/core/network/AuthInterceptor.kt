package com.xinlei.frontend.linkoria.app.core.network

import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        // chain contiene la solicitud original y permite continuar la cadena de interceptores

        //runBlocking en el interceptor porque intercept es una función normal de Java que no puede ser suspend
        val token = runBlocking { sessionManager.getAccessTokenOnce() }

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            // Si hay token, crea una nueva solicitud añadiendo el header Authorization con Bearer <token>
        } else {
            chain.request()
            // Si no hay token, usa la solicitud original sin modificar
        }

        return chain.proceed(request)
        // Envía la solicitud (modificada o no) y devuelve la respuesta
    }

}