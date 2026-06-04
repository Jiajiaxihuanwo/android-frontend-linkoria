package com.xinlei.frontend.linkoria.app.message.domain.usecase

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.core.storage.SupabaseStorageDataSource
import com.xinlei.frontend.linkoria.app.core.storage.UriToFileConverter
import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.message.domain.repository.MessageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: MessageRepository,
    private val storageDataSource: SupabaseStorageDataSource,
    private val sessionManager: SessionManager,
    private val uriToFileConverter: UriToFileConverter,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(
        conversationId: Long,
        content: String,
        messageType: String = "TEXT",
        replyToMessageId: Long? = null,
        imageUri: Uri? = null
    ): NetworkResult<Message> {

        val resolvedContent: String
        val resolvedType: String

        if (imageUri != null) {
            val userId = sessionManager.getUserIdOnce() ?: return NetworkResult.Error(null, "No hay sesión activa")

            val extension = context.contentResolver.getType(imageUri)
                ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
                ?: "jpg"

            val file = uriToFileConverter.convert(imageUri)
                ?: return NetworkResult.Error(null, "No se pudo leer la imagen")

            when (val uploadResult = storageDataSource.uploadChatImage(file, userId, extension)) {
                is NetworkResult.Success -> {
                    resolvedContent = uploadResult.data
                    resolvedType = "IMAGE"
                }
                is NetworkResult.Error -> return NetworkResult.Error(null, uploadResult.message ?: "Error al subir imagen")
                else -> return NetworkResult.Error(null, "Error inesperado")
            }
        } else {
            resolvedContent = content
            resolvedType = messageType
        }

        return repository.sendMessage(
            conversationId,
            resolvedContent,
            resolvedType,
            replyToMessageId
        )
    }
}