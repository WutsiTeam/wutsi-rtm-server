package com.wutsi.platform.rtm.websocket.processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.rtm.websocket.MessageProcessor
import com.wutsi.platform.rtm.websocket.RTMContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

abstract class AbstractMessageProcessor : MessageProcessor {
    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var context: RTMContext

    protected fun sendMessage(payload: Any, session: WebSocketSession): Boolean {
        try {
            val json = objectMapper.writeValueAsString(payload)
            session.sendMessage(TextMessage(json))
            return true
        } catch (ex: IllegalStateException) {
            LoggerFactory.getLogger(this::class.java).warn("Session#${session.id} is closed.", ex)
            context.detach(session)
            return false
        }
    }
}
