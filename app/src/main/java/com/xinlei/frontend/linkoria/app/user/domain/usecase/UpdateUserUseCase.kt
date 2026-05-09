package com.xinlei.frontend.linkoria.app.user.domain.usecase

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.core.storage.SupabaseStorageDataSource
import com.xinlei.frontend.linkoria.app.core.storage.UriToFileConverter
import com.xinlei.frontend.linkoria.app.user.data.remote.dto.UpdateUserRequest
import com.xinlei.frontend.linkoria.app.user.domain.UserRepository
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository,
    private val storageDataSource: SupabaseStorageDataSource,
    private val sessionManager: SessionManager,
    private val uriToFileConverter: UriToFileConverter,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(
        request: UpdateUserRequest,
        avatarUri: Uri? = null
    ): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)

        if (avatarUri != null) {
            val userId = sessionManager.getUserIdOnce() ?: run {
                emit(NetworkResult.Error(null, "No active session"))
                return@flow
            }

            val extension = context.contentResolver.getType(avatarUri)
                ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
                ?: "jpg"

            val file = uriToFileConverter.convert(avatarUri) ?: run {
                emit(NetworkResult.Error(null, "Could not read image"))
                return@flow
            }

            when (val uploadResult = storageDataSource.uploadUserIcon(file, userId, extension)) {
                is NetworkResult.Success -> {
                    emitAll(repository.updateUser(request.copy(avatarUrl = uploadResult.data)))
                }
                is NetworkResult.Error -> emit(uploadResult)
                is NetworkResult.Loading -> Unit
            }
        } else {
            emitAll(repository.updateUser(request))
        }
    }
}