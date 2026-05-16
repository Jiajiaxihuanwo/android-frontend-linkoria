package com.xinlei.frontend.linkoria.app.root.navigator

import android.app.Activity
import android.content.Intent
import com.xinlei.frontend.linkoria.app.chat.ui.ChatActivity
import com.xinlei.frontend.linkoria.app.friendship.ui.FriendsActivity
import com.xinlei.frontend.linkoria.app.root.SplashActivity
import javax.inject.Inject

class ProfileNavigator @Inject constructor() {
    fun openFriendShips(
        activity: Activity
    ) {
        val intent = Intent(activity, FriendsActivity::class.java)
        activity.startActivity(intent)
    }

    fun navigateToSplash(activity: Activity) {
        val intent = Intent(activity, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
    }
}