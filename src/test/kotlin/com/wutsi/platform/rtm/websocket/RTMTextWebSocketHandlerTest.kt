package com.wutsi.platform.rtm.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.dto.MessageType
import com.wutsi.platform.rtm.model.ChatMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class RTMTextWebSocketHandlerTest {
    private lateinit var session: WebSocketSession
    private lateinit var processor: MessageProcessor

    @MockBean
    private lateinit var factory: MessageProcessorFactory

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var handler: RTMTextWebSocketHandler

    @BeforeEach
    fun setUp() {
        session = mock()
        processor = mock()
        doReturn(processor).whenever(factory).get(any())
    }

    @Test
    fun handleMessage() {
        val msg = Message(
            type = MessageType.send,
            sessionId = "1111",
            roomId = "123",
            chatMessage = ChatMessage(
                id = UUID.randomUUID().toString()
            )
        )
        val payload = objectMapper.writeValueAsString(msg)
        handler.handleMessage(session, TextMessage(payload))

        val message = argumentCaptor<Message>()
        verify(processor).process(message.capture(), eq(session))
        assertEquals(msg.type, message.firstValue.type)
        assertEquals(msg.sessionId, message.firstValue.sessionId)
        assertEquals(msg.roomId, message.firstValue.roomId)
        assertEquals(msg.chatMessage?.id, message.firstValue.chatMessage?.id)
    }
}
