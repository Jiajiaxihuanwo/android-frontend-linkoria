package com.xinlei.frontend.linkoria.app.di

import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatNavigator
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Singleton
    @Binds
    abstract fun bindChatNavigator(
        chatNavigatorImpl: ChatNavigatorImpl
    ): ChatNavigator
}