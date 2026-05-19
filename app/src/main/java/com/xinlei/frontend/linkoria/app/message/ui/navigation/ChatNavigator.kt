package com.xinlei.frontend.linkoria.app.message.ui.navigation

import android.content.Context

interface ChatNavigator {
    fun openProfile(context: Context, chatType: String, targetId: String?, serverId: Long)
}