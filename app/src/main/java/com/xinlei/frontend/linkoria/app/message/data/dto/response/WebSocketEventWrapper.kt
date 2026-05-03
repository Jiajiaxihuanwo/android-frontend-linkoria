package com.xinlei.frontend.linkoria.app.message.data.dto.response

import com.google.gson.JsonElement

data class WebSocketEventWrapper(
    val type: String,
    val payload: JsonElement,
    val timestamp: Long
)