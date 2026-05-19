package com.xinlei.frontend.linkoria.app.root.navigator

import android.app.Activity
import android.content.Intent
import com.xinlei.frontend.linkoria.app.auth.ui.AuthActivity
import com.xinlei.frontend.linkoria.app.friendship.ui.FriendsActivity
import javax.inject.Inject

class ProfileNavigator @Inject constructor() {
    fun openFriendShips(
        activity: Activity
    ) {
        val intent = Intent(activity, FriendsActivity::class.java)
        activity.startActivity(intent)
    }

    fun navigateToAuth(activity: Activity) {
        val intent = Intent(activity, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
    }
}