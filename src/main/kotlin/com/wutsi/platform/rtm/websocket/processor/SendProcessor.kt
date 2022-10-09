package com.wutsi.platform.rtm.websocket.processor

import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.event.EventURN
import com.wutsi.platform.rtm.event.MessageSentEventPayload
import com.wutsi.platform.rtm.service.PushNotificationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class SendProcessor(
    private val eventStream: EventStream,
    private val pushNotificationService: PushNotificationService
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
        val sessions = context.findSessionsByRoom(message.roomId)
        sessions.forEach {
            if (it.id != session.id) {
                if (!sendDirectMessage(message, it)) {
                    sendPushNotification(message, it)
                }
            }
        }
    }

    private fun sendDirectMessage(message: Message, session: WebSocketSession): Boolean {
        try {
            LOGGER.info("Sending direct message to User#" + context.getUserId(session))
            return sendMessage(message.copy(sessionId = session.id), session)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send direct message to Session#${session.id}", ex)
            return false
        }
    }

    private fun sendPushNotification(message: Message, session: WebSocketSession) {
        message.chatMessage ?: return
        val recipientId = context.getUserId(session) ?: return
        try {
            LOGGER.info("Sending push notification to User#$recipientId")
            pushNotificationService.onMessageSent(message.chatMessage, recipientId.toLong())
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send push notification to User#$recipientId", ex)
        }
    }
}
