package com.xinlei.frontend.linkoria.app.di

import com.xinlei.frontend.linkoria.app.websocket.domain.port.TokenProvider
import com.xinlei.frontend.linkoria.app.websocket.infrastructure.adapter.StompTokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenProviderModule {

    @Binds
    @Singleton
    abstract fun bindTokenProvider(
        stompTokenProvider: StompTokenProvider
    ): TokenProvider
}