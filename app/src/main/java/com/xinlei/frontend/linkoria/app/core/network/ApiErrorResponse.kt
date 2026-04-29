package com.xinlei.frontend.linkoria.app.core.network

data class ApiErrorResponse (
    val status: Int,
    val message: String,
    val error: String
)