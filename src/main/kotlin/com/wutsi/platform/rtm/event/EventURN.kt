package com.wutsi.platform.rtm.event

enum class EventURN(val urn: String) {
    MESSAGE_SENT("urn:wutsi:event:rtm:message-sent"),
    MESSAGE_RECEIVED("urn:wutsi:event:rtm:message-received")
}
