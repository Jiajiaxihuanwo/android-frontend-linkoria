package com.xinlei.frontend.linkoria.app.di

import com.xinlei.frontend.linkoria.app.auth.data.remote.AuthApiService
import com.xinlei.frontend.linkoria.app.channel.data.remote.ChannelApiService
import com.xinlei.frontend.linkoria.app.conversation.data.remote.ConversationApiService
import com.xinlei.frontend.linkoria.app.core.network.AuthInterceptor
import com.xinlei.frontend.linkoria.app.core.util.Constants
import com.xinlei.frontend.linkoria.app.server.data.remote.ServerApiService
import com.xinlei.frontend.linkoria.app.user.data.remote.UserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
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

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideServerApiService(retrofit: Retrofit): ServerApiService =
        retrofit.create(ServerApiService::class.java)

    @Provides
    @Singleton
    fun provideChannelApiService(retrofit: Retrofit): ChannelApiService =
        retrofit.create(ChannelApiService::class.java)

    @Provides
    @Singleton
    fun provideConversationApiService(retrofit: Retrofit): ConversationApiService =
        retrofit.create(ConversationApiService::class.java)
}