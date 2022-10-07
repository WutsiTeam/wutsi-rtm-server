package com.wutsi.platform.rtm.event

data class MessageReceivedEventPayload(
    val serverId: String = "",
    val sessionId: String = "",
    val chatMessageId: String? = null
)
