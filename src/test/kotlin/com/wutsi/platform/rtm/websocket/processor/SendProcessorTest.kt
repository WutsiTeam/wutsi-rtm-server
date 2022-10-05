package com.wutsi.platform.rtm.websocket.processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.dto.MessageType
import com.wutsi.platform.rtm.model.ChatMessage
import com.wutsi.platform.rtm.model.ChatMessageType
import com.wutsi.platform.rtm.model.ChatUser
import com.wutsi.platform.rtm.websocket.RTMContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SendProcessorTest {
    @Autowired
    private lateinit var processor: SendProcessor

    @Autowired
    private lateinit var context: RTMContext

    private lateinit var session1: WebSocketSession
    private lateinit var session2: WebSocketSession

    private val roomId = "room1"
    private val sessionId1 = "session-100"
    private val sessionId2 = "session-200"
    private val userId1 = "100"
    private val userId2 = "200"
    val attributes = mutableMapOf<String, Any>()

    @BeforeEach
    fun setUp() {
        session1 = mock()
        doReturn(sessionId1).whenever(session1).id
        doReturn(attributes).whenever(session1).attributes
        context.attach(roomId, session1)

        session2 = mock()
        doReturn(sessionId2).whenever(session2).id
        doReturn(attributes).whenever(session2).attributes
        context.attach(roomId, session2)
    }

    @Test
    fun process() {
        val msg = Message(
            type = MessageType.send,
            roomId = roomId,
            sessionId = sessionId1,
            chatMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                roomId = roomId,
                type = ChatMessageType.text,
                author = ChatUser(
                    id = userId1
                ),
                text = "Hello world"
            )
        )
        processor.process(msg, session1)

        val result = argumentCaptor<TextMessage>()
        verify(session2).sendMessage(result.capture())

        val payload = ObjectMapper().readValue(result.firstValue.payload, Message::class.java)
        assertEquals(sessionId2, payload.sessionId)
        assertEquals(msg.chatMessage?.id, payload.chatMessage?.id)
        assertEquals(msg.chatMessage?.text, payload.chatMessage?.text)
    }
}
