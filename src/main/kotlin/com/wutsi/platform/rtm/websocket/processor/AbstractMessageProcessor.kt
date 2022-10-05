package com.wutsi.platform.rtm.websocket.processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.rtm.websocket.MessageProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

abstract class AbstractMessageProcessor : MessageProcessor {
    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    protected fun sendMessage(payload: Any, session: WebSocketSession) {
        val json = objectMapper.writeValueAsString(payload)
        session.sendMessage(TextMessage(json))
    }
}
