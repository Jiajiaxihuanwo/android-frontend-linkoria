package com.xinlei.frontend.linkoria.app.server.data.remote.dto

data class UpdateMemberRoleRequest(
    val newRole: String // Enviaremos el Enum como String (OWNER, ADMIN, MEMBER)
)