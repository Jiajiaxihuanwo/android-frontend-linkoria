package com.xinlei.frontend.linkoria.app.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.xinlei.frontend.linkoria.app.auth.data.remote.AuthApiService
import com.xinlei.frontend.linkoria.app.auth.data.remote.TokenRefreshApi
import com.xinlei.frontend.linkoria.app.channel.data.remote.ChannelApiService
import com.xinlei.frontend.linkoria.app.conversation.data.remote.ConversationApiService
import com.xinlei.frontend.linkoria.app.core.network.AuthInterceptor
import com.xinlei.frontend.linkoria.app.core.network.InstantDeserializer
import com.xinlei.frontend.linkoria.app.core.network.TokenAuthenticator
import com.xinlei.frontend.linkoria.app.core.util.Constants
import com.xinlei.frontend.linkoria.app.core.util.Constants.BASE_URL
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
import java.time.Instant
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGson(): Gson =  GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantDeserializer())
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    @Provides
    @Singleton
    @CleanOkHttp
    fun provideCleanOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideTokenRefreshApi(@CleanOkHttp cleanOkHttpClient: OkHttpClient, gson: Gson): TokenRefreshApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cleanOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TokenRefreshApi::class.java)
    }

    @Provides
    @Singleton
    @MainOkHttp
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(@MainOkHttp okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

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