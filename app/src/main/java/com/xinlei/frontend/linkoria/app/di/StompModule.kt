package com.xinlei.frontend.linkoria.app.di

import com.xinlei.frontend.linkoria.app.websocket.domain.port.StompClient
import com.xinlei.frontend.linkoria.app.websocket.infrastructure.adapter.StompClientImpl
import com.xinlei.frontend.linkoria.app.websocket.infrastructure.util.StompReconnectionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StompModule {

    @Provides
    @Singleton
    fun provideReconnectionManager(): StompReconnectionManager {
        return StompReconnectionManager(
            initialDelayMs = 1000,
            maxDelayMs = 30000,
            multiplier = 2.0,
            maxRetries = 10
        )
    }

    @Provides
    @Singleton
    fun provideStompClient(impl: StompClientImpl): StompClient {
        return impl
    }
}