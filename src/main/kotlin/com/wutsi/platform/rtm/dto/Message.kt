package com.wutsi.platform.rtm.dto

import com.wutsi.platform.rtm.model.ChatMessage

data class Message(
    val type: MessageType = MessageType.unknown,
    val roomId: String = "",
    val sessionId: String? = null,
    val chatMessage: ChatMessage? = null
)
