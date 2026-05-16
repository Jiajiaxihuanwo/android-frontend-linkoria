package com.xinlei.frontend.linkoria.app.root.navigator

import android.app.Activity
import android.content.Intent
import com.xinlei.frontend.linkoria.app.chat.ui.ChatActivity
import javax.inject.Inject

class AppNavigator @Inject constructor() {

    fun openDm(
        activity: Activity,
        conversationId: Long,
        userId: String
    ) {
        val intent = Intent(activity, ChatActivity::class.java).apply {
            putExtra("extra_conversation_id", conversationId)
            putExtra("extra_user_id",userId )
        }
        activity.startActivity(intent)
    }

    fun goBack(activity: Activity) {
        activity.finish()

    }

}