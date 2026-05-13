package com.xinlei.frontend.linkoria.app.server.domain.usecase

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.core.storage.SupabaseStorageDataSource
import com.xinlei.frontend.linkoria.app.core.storage.UriToFileConverter
import com.xinlei.frontend.linkoria.app.server.domain.ServerRepository
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class CreateServerUseCase @Inject constructor(
    private val repository: ServerRepository,
    private val storageDataSource: SupabaseStorageDataSource,
    private val sessionManager: SessionManager,
    private val uriToFileConverter: UriToFileConverter,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(
        name: String,
        iconUri: Uri? = null
    ): Flow<NetworkResult<Server>> = flow {
        emit(NetworkResult.Loading)

        val resolvedIconUrl: String? = if (iconUri != null) {
            val userId = sessionManager.getUserIdOnce() ?: run {
                emit(NetworkResult.Error(null, "No active session"))
                return@flow
            }

            val extension = context.contentResolver.getType(iconUri)
                ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
                ?: "jpg"

            val file = uriToFileConverter.convert(iconUri) ?: run {
                emit(NetworkResult.Error(null, "Could not read image"))
                return@flow
            }

            when (val uploadResult = storageDataSource.uploadServerIcon(file, userId, extension)) {
                is NetworkResult.Success -> uploadResult.data
                else -> {
                    emit(NetworkResult.Error(null, "Failed to upload server icon"))
                    return@flow
                }
            }
        } else null

        emitAll(
            repository.createServer(name, resolvedIconUrl)
        )
    }
}