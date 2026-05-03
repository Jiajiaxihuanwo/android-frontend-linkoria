package com.xinlei.frontend.linkoria.app.message.domain.usecase

import com.xinlei.frontend.linkoria.app.message.domain.repository.MessageRepository
import javax.inject.Inject

class ClearMessageCacheUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke() {
        repository.clearCache()
    }
}