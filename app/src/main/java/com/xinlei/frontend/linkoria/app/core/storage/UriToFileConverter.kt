package com.xinlei.frontend.linkoria.app.core.storage

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriToFileConverter @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    suspend fun convert(uri: Uri): File? = withContext(Dispatchers.IO) {
        try {
            val tempFile = File.createTempFile(
                "upload_",
                ".tmp",
                context.cacheDir
            )

            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext null

            tempFile
        } catch (e: Exception) {
            null
        }
    }
}