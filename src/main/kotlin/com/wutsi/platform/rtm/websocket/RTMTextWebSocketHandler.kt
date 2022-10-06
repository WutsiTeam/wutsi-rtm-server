package com.wutsi.platform.rtm.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.rtm.dto.Message
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class RTMTextWebSocketHandler(
    private val factory: MessageProcessorFactory,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val logger = DefaultKVLogger()
        try {
            val msg = objectMapper.readValue(message.payload, Message::class.java)
            handleTextMessage(session, msg, logger)
        } catch (ex: Exception) {
            logger.setException(ex)
        } finally {
            logger.log()
        }
    }

    private fun handleTextMessage(session: WebSocketSession, message: Message, logger: KVLogger) {
        logger.add("session_id", session.id)
        logger.add("type", message.type)
        logger.add("roomId", message.roomId)
        val processor = factory.get(message.type)
        processor.process(message, session)
    }
}
