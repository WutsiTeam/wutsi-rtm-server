package com.wutsi.platform.rtm.model

/**
 * All values in lowercase to match https://pub.dev/documentation/flutter_chat_types/latest/flutter_chat_types/MessageType.html
 */
enum class ChatMessageType {
    unsupported,
    custom,
    file,
    image,
    text
}
