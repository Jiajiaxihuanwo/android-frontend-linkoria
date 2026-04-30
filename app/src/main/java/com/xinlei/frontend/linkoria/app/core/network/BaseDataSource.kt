package com.xinlei.frontend.linkoria.app.core.network

import com.google.gson.Gson
import retrofit2.HttpException

abstract class BaseDataSource {
    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): NetworkResult<T> {
        return try {
            NetworkResult.Success(apiCall())
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, ApiErrorResponse::class.java)
                errorResponse.message
            } catch (parseException: Exception) {
                "Error inesperado del servidor"
            }
            NetworkResult.Error(code = e.code(), message = errorMessage)
        } catch (e: Exception) {
            NetworkResult.Error(code = null, message = e.message)
        }
    }
}