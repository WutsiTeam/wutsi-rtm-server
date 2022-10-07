package com.wutsi.platform.rtm.websocket.processor

import com.wutsi.platform.rtm.dto.Message
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class ReceivedProcessor : AbstractMessageProcessor() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReceivedProcessor::class.java)
    }

    override fun process(message: Message, session: WebSocketSession) {
        message.userId ?: return

        // Notify the sender
        val senderId = message.chatMessage?.author?.id
            ?: return

        val sessions = context.findSessionsByUser(senderId)
        sessions.forEach {
            try {
                sendMessage(message, it)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to send message to Session#${it.id}", ex)
            }
        }
    }
}
