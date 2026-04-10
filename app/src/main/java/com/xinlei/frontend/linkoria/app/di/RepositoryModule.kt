package com.xinlei.frontend.linkoria.app.di

import com.xinlei.frontend.linkoria.app.auth.data.AuthRepositoryImpl
import com.xinlei.frontend.linkoria.app.auth.domain.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ) : AuthRepository
}