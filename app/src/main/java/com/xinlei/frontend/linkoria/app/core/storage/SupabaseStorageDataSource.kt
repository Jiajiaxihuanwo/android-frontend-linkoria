package com.xinlei.frontend.linkoria.app.core.storage

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
            deleteUserIcons(path, fullPath)
            NetworkResult.Success(publicUrl)
        } catch (e: Exception) {
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

    /**
     * Sube el icono de un servidor al bucket correspondiente.
     *
     * @param file      Archivo temporal generado por [UriToFileConverter].
     * @param serverId  ID del servidor, usado como nombre de archivo.
     * @param extension Extensión real del archivo (ej: "jpg", "png").
     */
    suspend fun uploadServerIcon(
        file: File,
        serverId: String,
        extension: String
    ): NetworkResult<String> = uploadFile(
        bucket = SERVER_ICONS_BUCKET,
        path = serverId,
        file = file,
        extension = extension
    )

    private suspend fun deleteUserIcons(userId: String, currentFileName: String? = null) {
        val bucket = supabaseClient.storage[USER_ICONS_BUCKET]

        // Al estar en la raíz, el prefijo es directamente el ID del usuario
        val files = bucket.list()

        val pathsToDelete = files
            .map { it.name }
            .filter { it.startsWith("${userId}_") && it != currentFileName }

        if (pathsToDelete.isNotEmpty()) {
            bucket.delete(pathsToDelete)
        }
    }
}