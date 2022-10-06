package com.wutsi.platform.rtm.websocket.processor

import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.event.EventURN
import com.wutsi.platform.rtm.event.MessageSentEventPayload
import com.wutsi.platform.rtm.websocket.RTMContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class SendProcessor(
    private val context: RTMContext,
    private val eventStream: EventStream
) : AbstractMessageProcessor() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SendProcessor::class.java)
    }

    override fun process(message: Message, session: WebSocketSession) {
        // Push to the queue for persistence
        val payload = MessageSentEventPayload(
            serverId = context.serverId,
            sessionId = session.id,
            chatMessage = message.chatMessage
        )
        eventStream.publish(EventURN.MESSAGE_SENT.urn, payload)

        // Broadcast message to other participants in the room
        val sessions = context.findSessionByRoom(message.roomId)
        sessions.forEach {
            if (it.id != session.id) {
                try {
                    sendMessage(message.copy(sessionId = it.id), it)
                } catch (ex: IllegalStateException) {
                    LOGGER.warn("Session#${it.id} is closed. detaching from context", ex)
                    context.detach(session)
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to send message to Session#${it.id}", ex)
                }
            }
        }
    }
}
