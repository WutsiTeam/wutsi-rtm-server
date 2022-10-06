package com.wutsi.platform.rtm.websocket.processor

import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.websocket.RTMContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class ReceivedProcessor(
    private val context: RTMContext
) : AbstractMessageProcessor() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReceivedProcessor::class.java)
    }

    override fun process(message: Message, session: WebSocketSession) {
        message.userId ?: return

        // Notify the sender
        val sessions = context.findSessionsByUser(message.userId)
        sessions.forEach {
            try {
                sendMessage(message, it)
            } catch (ex: IllegalStateException) {
                LOGGER.warn("Session#${it.id} is closed. detaching from context", ex)
                context.detach(session)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to send message to Session#${it.id}", ex)
            }
        }
    }
}
