package com.wutsi.platform.rtm.websocket.processor

import com.wutsi.platform.rtm.dto.Message
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class HelloProcessor : AbstractMessageProcessor() {
    override fun process(message: Message, session: WebSocketSession) {
        val sessionId = session.id
        context.attach(message.roomId, message.userId, session)

        val response = Message(
            type = message.type,
            roomId = message.roomId,
            sessionId = sessionId
        )
        sendMessage(response, session)
    }
}
