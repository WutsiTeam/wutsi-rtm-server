package com.wutsi.platform.rtm.websocket.processor

import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.websocket.RTMContext
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class SendProcessor(
    private val context: RTMContext,
    private val logger: KVLogger
) : AbstractMessageProcessor() {
    override fun process(message: Message, session: WebSocketSession) {
        val sessions = context.findSessionByRoom(message.roomId)
        logger.add("session_ids", sessions.map { it.id })
        sessions.forEach {
            if (it.id != session.id) {
                sendMessage(message.copy(sessionId = it.id), it)
            }
        }
    }
}
