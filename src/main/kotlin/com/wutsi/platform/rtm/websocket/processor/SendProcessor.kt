package com.wutsi.platform.rtm.websocket.processor

import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.event.EventURN
import com.wutsi.platform.rtm.event.MessageSentEventPayload
import com.wutsi.platform.rtm.model.ChatMessage
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
        val recipientId = getRecipientId(message)
        if (recipientId == null) {
            sendToRoom(message, session)
        } else {
            sendToRecipient(message, recipientId)
        }
    }

    private fun sendToRecipient(message: Message, recipientId: String) {
        val sessions = context.findSessionsByUser(recipientId)
        var sent = 0
        sessions.forEach {
            if (sendToSession(message, it)) {
                sent++
            }
        }

        if (sent == 0 && message.chatMessage != null) {
            sendPushNotification(message.chatMessage, recipientId)
        }
    }

    private fun sendToRoom(message: Message, session: WebSocketSession) {
        val sessions = context.findSessionsByRoom(message.roomId)
        sessions.forEach {
            if (it.id != session.id) {
                if (!sendToSession(message, it)) {
                    if (message.chatMessage != null) {
                        val recipientId = context.getUserId(it)
                        if (recipientId != null) {
                            sendPushNotification(message.chatMessage, recipientId)
                        }
                    }
                }
            }
        }
    }

    private fun sendToSession(message: Message, session: WebSocketSession): Boolean =
        try {
            LOGGER.info("Sending direct message to Session#${session.id}")
            sendMessage(message.copy(sessionId = session.id), session)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send direct message to User#${session.id}", ex)
            false
        }

    private fun sendPushNotification(chatMessage: ChatMessage, recipientId: String): Boolean {
        try {
            LOGGER.info("Sending push notification to User#$recipientId")
            pushNotificationService.onMessageSent(chatMessage, recipientId.toLong())
            return true
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send push notification to User#$recipientId", ex)
            return false
        }
    }

    private fun getRecipientId(message: Message): String? =
        message.chatMessage?.metadata?.get("recipientId")?.toString()
}
