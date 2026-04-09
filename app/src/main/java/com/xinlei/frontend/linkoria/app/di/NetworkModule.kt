package com.xinlei.frontend.linkoria.app.di

import com.xinlei.frontend.linkoria.app.core.network.AuthInterceptor
import com.xinlei.frontend.linkoria.app.core.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttp(authInterceptor: AuthInterceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttp: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}