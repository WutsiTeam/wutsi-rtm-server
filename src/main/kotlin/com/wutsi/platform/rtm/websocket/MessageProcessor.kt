package com.wutsi.platform.rtm.websocket

import com.wutsi.platform.rtm.dto.Message
import org.springframework.web.socket.WebSocketSession

interface MessageProcessor {
    fun process(message: Message, session: WebSocketSession)
}
