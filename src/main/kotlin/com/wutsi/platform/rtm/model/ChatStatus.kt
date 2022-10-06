package com.wutsi.platform.rtm.model

/**
 * All values in lowercase to match https://pub.dev/documentation/flutter_chat_types/latest/flutter_chat_types/Status.html
 */
enum class ChatStatus {
    delivered,
    error,
    seen,
    sending,
    sent
}
