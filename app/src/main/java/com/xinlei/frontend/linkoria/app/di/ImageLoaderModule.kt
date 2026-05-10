package com.xinlei.frontend.linkoria.app.di

import android.content.Context
import com.xinlei.frontend.linkoria.app.core.ui.image.GlideImageLoader
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageLoaderModule {

    @Singleton
    @Binds
    abstract fun provideImageLoader(
        glideImageLoader: GlideImageLoader
    ): ImageLoader
}