package com.xinlei.frontend.linkoria.app.root.navigator

import android.app.Activity
import android.content.Intent
import com.xinlei.frontend.linkoria.app.chat.ui.ChatActivity
import com.xinlei.frontend.linkoria.app.root.MainActivity
import javax.inject.Inject

class AppNavigator @Inject constructor() {

    fun navigateToDashboard(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        activity.startActivity(intent)
    }
}