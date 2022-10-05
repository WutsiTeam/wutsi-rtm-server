package com.wutsi.platform.rtm.model

data class ChatMessage(
    val id: String = "",
    val roomId: String? = null,
    val remoteId: String? = null,
    val author: ChatUser = ChatUser(),
    val createdAt: Int? = null,
    val updatedAt: Int? = null,
    val showStatus: Boolean = false,
    val status: ChatStatus? = null,
    val type: ChatMessageType = ChatMessageType.text,
    val repliedMessage: ChatMessage? = null,
    val metadata: Map<String, Any>? = null,

    // TextMessage
    val text: String? = null,
    val previewData: ChatPreviewData? = null,

    // ImageMessage
    val width: Double? = null,
    val height: Double? = null,

    // FileMessage
    val uri: String? = null
)
