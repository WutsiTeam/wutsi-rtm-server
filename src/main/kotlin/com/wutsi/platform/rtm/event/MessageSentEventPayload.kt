package com.wutsi.platform.rtm.event

import com.wutsi.platform.rtm.model.ChatMessage

data class MessageSentEventPayload(
    val sessionId: String,
    val chatMessage: ChatMessage? = null
)
