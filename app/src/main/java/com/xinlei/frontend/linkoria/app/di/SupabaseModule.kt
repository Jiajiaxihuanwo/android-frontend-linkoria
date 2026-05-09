package com.xinlei.frontend.linkoria.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://trtpzsexfehwiiiknxph.supabase.co",
        supabaseKey = "sb_publishable_xtUF-vi1kuulRYJf03ubUg_SS2axtk-"
    ) {
        install(Storage)
    }
}