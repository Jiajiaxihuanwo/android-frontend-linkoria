package com.xinlei.frontend.linkoria.app.core.storage

import android.util.Log
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseStorageDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    companion object {
        private const val USER_ICONS_BUCKET = "user_icons"
        private const val SERVER_ICONS_BUCKET = "server_icons"
    }
    private suspend fun uploadFile(
        bucket: String,
        path: String,
        file: File,
        extension: String = "jpg"
    ): NetworkResult<String> {
        return try {
            val fullPath = "${path}_${System.currentTimeMillis()}.$extension"

            supabaseClient.storage[bucket].upload(fullPath, file.readBytes())

            val publicUrl = supabaseClient.storage[bucket].publicUrl(fullPath)
            deletePreviousFiles(bucket, path, fullPath)
            NetworkResult.Success(publicUrl)
        } catch (e: Exception) {
            Log.e("error", e.printStackTrace().toString())
            NetworkResult.Error(null, e.message)
        } finally {
            // Limpia el archivo temporal independientemente del resultado
            file.delete()
        }
    }

    suspend fun uploadUserIcon(
        file: File,
        userId: String,
        extension: String = "jpg"
    ): NetworkResult<String> = uploadFile(
        bucket = USER_ICONS_BUCKET,
        path = userId,
        file = file,
        extension = extension
    )

    suspend fun uploadServerIcon(
        file: File,
        serverId: String,
        extension: String = "jpg"
    ): NetworkResult<String> = uploadFile(
        bucket = SERVER_ICONS_BUCKET,
        path = serverId,
        file = file,
        extension = extension
    )

    private suspend fun deletePreviousFiles(bucket: String, prefix: String, currentFileName: String) {
        val storageBucket = supabaseClient.storage[bucket]
        val pathsToDelete = storageBucket.list()
            .map { it.name }
            .filter { it.startsWith("${prefix}_") && it != currentFileName }

        if (pathsToDelete.isNotEmpty()) {
            storageBucket.delete(pathsToDelete)
        }
    }
}