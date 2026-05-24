package com.xinlei.frontend.linkoria.app.message.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
import com.xinlei.frontend.linkoria.app.channel.domain.usecase.GetChannelByIdUseCase
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs.Companion.TYPE_CHANNEL
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs.Companion.TYPE_DM
import com.xinlei.frontend.linkoria.app.conversation.domain.usecase.GetChannelConversationUseCase
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.core.storage.SupabaseStorageDataSource
import com.xinlei.frontend.linkoria.app.core.storage.UriToFileConverter
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.message.domain.model.MessageUpdate
import com.xinlei.frontend.linkoria.app.message.domain.usecase.GetMessagesUseCase
import com.xinlei.frontend.linkoria.app.message.domain.usecase.ObserveMessageUpdatesUseCase
import com.xinlei.frontend.linkoria.app.message.domain.usecase.SendMessageUseCase
import com.xinlei.frontend.linkoria.app.message.domain.usecase.SubscribeToConversationUseCase
import com.xinlei.frontend.linkoria.app.message.domain.usecase.UnsubscribeFromConversationUseCase
import com.xinlei.frontend.linkoria.app.message.ui.adapter.ChatListItem
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs
import com.xinlei.frontend.linkoria.app.typing.domain.model.TypingAction
import com.xinlei.frontend.linkoria.app.typing.domain.model.TypingEvent
import com.xinlei.frontend.linkoria.app.typing.domain.usecase.ObserveTypingEventsUseCase
import com.xinlei.frontend.linkoria.app.typing.domain.usecase.SendTypingStartUseCase
import com.xinlei.frontend.linkoria.app.typing.domain.usecase.SendTypingStopUseCase
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import com.xinlei.frontend.linkoria.app.user.domain.usecase.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getChannelByIdUseCase: GetChannelByIdUseCase,
    private val getChannelConversationUseCase: GetChannelConversationUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeMessagesUpdatesUseCase: ObserveMessageUpdatesUseCase,
    private val unsubscribeFromConversationUseCase: UnsubscribeFromConversationUseCase,
    private val subscribeToConversationUseCase: SubscribeToConversationUseCase,
    private val sessionManager: SessionManager,
    private val subscribeToTypingUseCase: ObserveTypingEventsUseCase,
    private val sendTypingStartUseCase: SendTypingStartUseCase,
    private val sendTypingStopUseCase: SendTypingStopUseCase
) : ViewModel() {

    private val _dmState = MutableStateFlow<UiState<User?>>(UiState.Idle)
    val dmState: StateFlow<UiState<User?>> = _dmState.asStateFlow()

    private val _channelState = MutableStateFlow<UiState<Channel>>(UiState.Idle)
    val channelState: StateFlow<UiState<Channel>> = _channelState.asStateFlow()

    private val _conversationId = MutableStateFlow<Long?>(null)
    val conversationId: StateFlow<Long?> = _conversationId.asStateFlow()

    private val _messagesState = MutableStateFlow<UiState<List<ChatListItem>>>(UiState.Idle)
    val messagesState: StateFlow<UiState<List<ChatListItem>>> = _messagesState.asStateFlow()

    private val _sendState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val sendState: StateFlow<UiState<Unit>> = _sendState.asStateFlow()

    private val userCache = mutableMapOf<String, User>()
    private var currentUserId: String? = null

    private var nextCursor: Long? = null
    private var hasMore: Boolean = true

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _typingUsers = MutableStateFlow<Set<User>>(emptySet())
    val typingUsers: StateFlow<Set<User>> = _typingUsers.asStateFlow()

    fun init(args: ChatArgs) {
        viewModelScope.launch {
            currentUserId = sessionManager.getUserIdOnce()
        }
        when (args.chatType) {
            TYPE_DM -> {
                if (args.conversationId != -1L && !args.targetId.isNullOrEmpty()) {
                    initDmChat(args.conversationId, args.targetId)
                }
            }
            TYPE_CHANNEL -> {
                if (args.serverId != -1L && args.channelId != -1L) {
                    initChannelChat(args.serverId, args.channelId)
                }
            }
        }
    }

    fun initDmChat(conversationId: Long, targetId: String) {
        _conversationId.value = conversationId
        loadUserProfile(targetId)
        viewModelScope.launch {
            subscribeToConversation(conversationId)
        }
    }

    fun initChannelChat(serverId: Long, channelId: Long) {
        viewModelScope.launch {
            resolveChannelConversation(channelId)
            loadChannel(serverId, channelId)
            _conversationId.value?.let { subscribeToConversation(it) }
        }
    }

    // — Mensajes — //

    private suspend fun subscribeToConversation(conversationId: Long) {
        when (val result = subscribeToConversationUseCase(conversationId)) {
            is NetworkResult.Success -> {
                loadMessages(conversationId)
                observeMessageUpdates(conversationId)
                observeTypingEvents(conversationId)
                hideShimmers()
            }
            is NetworkResult.Error -> _messagesState.value = UiState.Error(result.message ?: "Error al suscribirse")
            else -> Unit
        }
    }
    private fun observeMessageUpdates(conversationId: Long) {
        viewModelScope.launch {
            observeMessagesUpdatesUseCase(conversationId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> handleMessageUpdate(result.data)
                    is NetworkResult.Error   -> Unit
                    else -> Unit
                }
            }
        }
    }

    fun onTypingStart() {
        val conversationId = _conversationId.value ?: return
        viewModelScope.launch {
            sendTypingStartUseCase(conversationId)
        }
    }

    fun onTypingStop() {
        val conversationId = _conversationId.value ?: return
        viewModelScope.launch {
            sendTypingStopUseCase(conversationId)
        }
    }

    private fun observeTypingEvents(conversationId: Long) {
        viewModelScope.launch {
            subscribeToTypingUseCase(conversationId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> handleTypingEvent(result.data)
                    is NetworkResult.Error -> Unit
                    else -> Unit
                }
            }
        }
    }

    private fun handleTypingEvent(event: TypingEvent) {
        if (event.userId.toString() == currentUserId) return
        viewModelScope.launch {
            when (event.action) {
                TypingAction.START -> {
                    val user = userCache[event.userId.toString()]
                        ?: run {
                            getUserByIdUseCase(event.userId.toString()).collect { result ->
                                if (result is NetworkResult.Success) {
                                    userCache[event.userId.toString()] = result.data
                                }
                            }
                            userCache[event.userId.toString()] ?: return@launch
                        }
                    _typingUsers.value += user
                }
                TypingAction.STOP -> {
                    val user = userCache[event.userId.toString()] ?: return@launch
                    _typingUsers.value -= user
                }
            }
        }
    }

    private fun hideShimmers() {

    }

    private suspend fun handleMessageUpdate(update: MessageUpdate) {
        when (update) {
            is MessageUpdate.Created -> {
                resolveUsers(listOf(update.message))
                val sender = userCache[update.message.userId] ?: return
                val newItem = if (update.message.userId == currentUserId) {
                    ChatListItem.MessageSent(update.message, sender)
                } else {
                    ChatListItem.MessageReceived(update.message, sender)
                }
                val current = (_messagesState.value as? UiState.Success)?.data ?: emptyList()
                _messagesState.value = UiState.Success(current + newItem).also {
                    Log.d("CHAT_STATE", "handleMessageUpdate -> lista size: ${(current + newItem).size}")
                }
            }
            is MessageUpdate.Edited  -> Unit // para más adelante
            is MessageUpdate.Deleted -> Unit // para más adelante
            else -> Unit
        }
    }

    private fun loadMessages(conversationId: Long) {
        viewModelScope.launch {
            _messagesState.value = UiState.Loading
            getMessagesUseCase(conversationId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        nextCursor = result.data.nextCursor
                        hasMore = result.data.hasMore
                        val sorted = result.data.messages.sortedBy { it.createdAt }
                        resolveUsers(sorted)
                        _messagesState.value = UiState.Success(mapToListItems(sorted))
                    }
                    is NetworkResult.Error -> _messagesState.value = UiState.Error(result.message ?: "Error al cargar mensajes")
                    else -> Unit
                }
            }
        }
    }

    fun loadMoreMessages() {
        if (_isLoadingMore.value || !hasMore) return
        val conversationId = _conversationId.value ?: return
        val cursor = nextCursor ?: return
        viewModelScope.launch {
            _isLoadingMore.value = true
            getMessagesUseCase(conversationId, cursor = cursor).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        nextCursor = result.data.nextCursor
                        hasMore = result.data.hasMore
                        val sorted = result.data.messages.sortedBy { it.createdAt }
                        resolveUsers(sorted)
                        val current = (_messagesState.value as? UiState.Success)?.data ?: emptyList()
                        _messagesState.value = UiState.Success(mapToListItems(sorted) + current)
                        _isLoadingMore.value = false
                    }
                    is NetworkResult.Error -> {
                        _messagesState.value = UiState.Error(result.message ?: "Error al paginar")
                        _isLoadingMore.value = false
                    }
                    else -> Unit
                }
            }
        }
    }

    fun sendMessage(content: String, messageType: String = "TEXT") {
        val conversationId = _conversationId.value ?: return
        viewModelScope.launch {
            _sendState.value = UiState.Loading
            when (val result = sendMessageUseCase(conversationId, content, messageType)) {
                is NetworkResult.Success -> {
                    _sendState.value = UiState.Success(Unit)
                    val sender = userCache[result.data.userId] ?: return@launch
                    val newItem = ChatListItem.MessageSent(result.data, sender)
                    val current = (_messagesState.value as? UiState.Success)?.data ?: emptyList()
                    _messagesState.value = UiState.Success(listOf(newItem) + current)
                }
                is NetworkResult.Error -> _sendState.value = UiState.Error(result.message ?: "Error al enviar")
                else -> Unit
            }
        }
    }

    fun sendImageMessage(uri: Uri) {
        val conversationId = _conversationId.value ?: return
        viewModelScope.launch {
            _sendState.value = UiState.Loading
            when (val result = sendMessageUseCase(conversationId, "", imageUri = uri)) {
                is NetworkResult.Success -> _sendState.value = UiState.Success(Unit)
                is NetworkResult.Error -> _sendState.value = UiState.Error(result.message ?: "Error al enviar")
                else -> Unit
            }
        }
    }

    // — Helpers — //

    private suspend fun resolveUsers(messages: List<Message>) {
        val unknownIds = messages
            .map { it.userId }
            .distinct()
            .filter { it !in userCache }

        unknownIds.forEach { userId ->
            getUserByIdUseCase(userId).collect { result ->
                if (result is NetworkResult.Success) {
                    userCache[userId] = result.data
                }
            }
        }
    }

    private fun mapToListItems(messages: List<Message>): List<ChatListItem> {
        val items = mutableListOf<ChatListItem>()
        var lastDate: LocalDate? = null

        messages.forEach { message ->
            val sender = userCache[message.userId] ?: return@forEach
            val messageDate = message.createdAt
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            if (messageDate != lastDate) {
                items.add(ChatListItem.DateHeader(message.createdAt))
                lastDate = messageDate
            }

            if (message.userId == currentUserId) {
                items.add(ChatListItem.MessageSent(message, sender))
            } else {
                items.add(ChatListItem.MessageReceived(message, sender))
            }
        }

        return items
    }

    private fun loadUserProfile(targetId: String) {
        viewModelScope.launch {
            _dmState.value = UiState.Loading
            getUserByIdUseCase(targetId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> _dmState.value = UiState.Success(result.data)
                    is NetworkResult.Error -> _dmState.value = UiState.Error(result.message ?: "Error desconocido")
                    else -> Unit
                }
            }
        }
    }

    private suspend fun resolveChannelConversation(channelId: Long) {
        getChannelConversationUseCase(channelId).collect { result ->
            when (result) {
                is NetworkResult.Success -> _conversationId.value = result.data.id
                is NetworkResult.Error -> _channelState.value = UiState.Error(result.message ?: "Error al obtener conversación")
                else -> Unit
            }
        }
    }

    private fun loadChannel(serverId: Long, channelId: Long) {
        viewModelScope.launch {
            _channelState.value = UiState.Loading
            getChannelByIdUseCase(serverId, channelId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> _channelState.value = UiState.Success(result.data)
                    is NetworkResult.Error -> _channelState.value = UiState.Error(result.message ?: "Error al cargar canal")
                    else -> Unit
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            _conversationId.value?.let { unsubscribeFromConversationUseCase(it) }
        }
    }
}