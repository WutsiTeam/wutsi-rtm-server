package com.wutsi.platform.rtm.model

data class ChatUser(
    val id: String = "",
    val createdAt: Long = -1,
    val firstName: String? = null,
    val lastName: String? = null,
    val imageUrl: String? = null,
    val lastSeen: Long? = null,
    val updatedAt: Long? = null,
    val metadata: Map<String, Any>? = null
)
