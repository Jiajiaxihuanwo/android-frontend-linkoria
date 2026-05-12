package com.xinlei.frontend.linkoria.app.root.navigator

import android.app.Activity
import android.content.Intent
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.chat.ui.ChatActivity
import javax.inject.Inject

class AppNavigator @Inject constructor() {

    fun openDm(
        activity: Activity,
        conversationId: Long,
        name: String,
        avatarUrl: String?,
    ) {
        val intent = Intent(activity, ChatActivity::class.java).apply {
            putExtra("extra_conversation_id", conversationId)
            putExtra("extra_user_name", name)
            putExtra("extra_user_avatar", avatarUrl)
        }
        activity.startActivity(intent)
    }

    fun goBack(activity: Activity) {
        activity.finish()

    }

}