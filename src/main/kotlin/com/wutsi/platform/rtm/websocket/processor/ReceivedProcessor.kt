package com.wutsi.platform.rtm.websocket.processor

import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.event.EventURN
import com.wutsi.platform.rtm.event.MessageReceivedEventPayload
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class ReceivedProcessor(
    private val eventStream: EventStream
) : AbstractMessageProcessor() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReceivedProcessor::class.java)
    }

    override fun process(message: Message, session: WebSocketSession) {
        message.userId ?: return

        // Push to the queue for persistence
        val payload = MessageReceivedEventPayload(
            serverId = context.serverId,
            sessionId = session.id,
            chatMessageId = message.chatMessage?.id
        )
        eventStream.publish(EventURN.MESSAGE_RECEIVED.urn, payload)

        // Notify the sender
        val senderId = message.chatMessage?.author?.id
            ?: return

        val sessions = context.findSessionsByUser(senderId)
        sessions.forEach {
            try {
                sendMessage(message, it)
                LOGGER.info("Message sent to User#$senderId")
            } catch (ex: Exception) {
                LOGGER.warn("Unable to send message to User#$senderId", ex)
            }
        }
    }
}
