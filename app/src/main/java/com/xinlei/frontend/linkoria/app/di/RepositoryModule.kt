package com.xinlei.frontend.linkoria.app.di

import com.xinlei.frontend.linkoria.app.auth.data.AuthRepositoryImpl
import com.xinlei.frontend.linkoria.app.auth.domain.AuthRepository
import com.xinlei.frontend.linkoria.app.channel.data.ChannelRepositoryImpl
import com.xinlei.frontend.linkoria.app.channel.domain.ChannelRepository
import com.xinlei.frontend.linkoria.app.conversation.data.ConversationRepositoryImpl
import com.xinlei.frontend.linkoria.app.conversation.domain.ConversationRepository
import com.xinlei.frontend.linkoria.app.friendship.data.FriendshipRepositoryImpl
import com.xinlei.frontend.linkoria.app.friendship.domain.FriendshipRepository
import com.xinlei.frontend.linkoria.app.server.data.ServerRepositoryImpl
import com.xinlei.frontend.linkoria.app.server.domain.ServerRepository
import com.xinlei.frontend.linkoria.app.user.data.UserRepositoryImpl
import com.xinlei.frontend.linkoria.app.user.domain.UserRepository
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

    @Singleton
    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ) : UserRepository

    @Singleton
    @Binds
    abstract fun bindServerRepository(
        serverRepositoryImpl: ServerRepositoryImpl
    ) : ServerRepository

    @Singleton
    @Binds
    abstract fun bindChannelRepository(
        channelRepositoryImpl: ChannelRepositoryImpl
    ) : ChannelRepository

    @Binds
    @Singleton
    abstract fun bindConversationRepository(
        conversationRepositoryImpl: ConversationRepositoryImpl
    ): ConversationRepository

    @Singleton
    @Binds
    abstract fun bindFriendshipRepository(
        friendshipRepositoryImpl: FriendshipRepositoryImpl
    ): FriendshipRepository
}