package com.wutsi.platform.rtm.model

data class ChatUser(
    val id: String = "",
    val createdAt: Int = -1,
    val firstName: String? = null,
    val lastName: String? = null,
    val imageUrl: String? = null,
    val lastSeen: Int? = null,
    val updatedAt: Int? = null,
    val metadata: Map<String, Any>? = null
)
