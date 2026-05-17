package com.xinlei.frontend.linkoria.app.root.navigator

import android.app.Activity
import android.content.Intent
import com.xinlei.frontend.linkoria.app.server.ui.ServerMemberActivity
import javax.inject.Inject
import kotlin.jvm.java

class ServerMemberNavigator @Inject constructor() {

    fun openServerMembers(activity: Activity, serverId: Long) {
        val intent = Intent(activity, ServerMemberActivity::class.java).apply {
            putExtra(ServerMemberActivity.EXTRA_SERVER_ID, serverId)
        }
        activity.startActivity(intent)
    }
}