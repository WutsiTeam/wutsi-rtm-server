package com.wutsi.platform.rtm.websocket.processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.dto.MessageType
import com.wutsi.platform.rtm.event.EventURN
import com.wutsi.platform.rtm.event.MessageSentEventPayload
import com.wutsi.platform.rtm.model.ChatMessage
import com.wutsi.platform.rtm.model.ChatMessageType
import com.wutsi.platform.rtm.model.ChatUser
import com.wutsi.platform.rtm.websocket.RTMContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SendProcessorTest {
    @Autowired
    private lateinit var processor: SendProcessor

    @Autowired
    private lateinit var context: RTMContext

    @MockBean
    private lateinit var eventStream: EventStream

    private lateinit var session1: WebSocketSession
    private lateinit var session2: WebSocketSession
    private lateinit var session3: WebSocketSession

    private val roomId = "room1"
    private val sessionId1 = "session-100"
    private val sessionId2 = "session-200"
    private val sessionId3 = "session-300"
    private val userId1 = "100"
    private val userId2 = "200"
    private val userId3 = "300"
    private val attributes1 = mutableMapOf<String, Any>()
    private val attributes2 = mutableMapOf<String, Any>()
    private val attributes3 = mutableMapOf<String, Any>()

    private val msg = Message(
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

    @BeforeEach
    fun setUp() {
        session1 = mock()
        doReturn(sessionId1).whenever(session1).id
        doReturn(attributes1).whenever(session1).attributes
        context.attach(roomId, userId1, session1)

        session2 = mock()
        doReturn(sessionId2).whenever(session2).id
        doReturn(attributes2).whenever(session2).attributes
        context.attach(roomId, userId2, session2)

        session3 = mock()
        doReturn(sessionId3).whenever(session3).id
        doReturn(attributes3).whenever(session3).attributes
        context.attach(roomId, userId3, session3)
    }

    @Test
    fun process() {
        processor.process(msg, session1)

        verify(session1, never()).sendMessage(any())

        val result = argumentCaptor<TextMessage>()
        verify(session2).sendMessage(result.capture())
        val payload2 = ObjectMapper().readValue(result.firstValue.payload, Message::class.java)
        assertEquals(sessionId2, payload2.sessionId)
        assertEquals(msg.chatMessage?.id, payload2.chatMessage?.id)
        assertEquals(msg.chatMessage?.text, payload2.chatMessage?.text)

        verify(session3).sendMessage(result.capture())
        val payload3 = ObjectMapper().readValue(result.secondValue.payload, Message::class.java)
        assertEquals(sessionId3, payload3.sessionId)
        assertEquals(msg.chatMessage?.id, payload3.chatMessage?.id)
        assertEquals(msg.chatMessage?.text, payload3.chatMessage?.text)

        val payload = argumentCaptor<MessageSentEventPayload>()
        verify(eventStream).publish(eq(EventURN.MESSAGE_SENT.urn), payload.capture())
        assertEquals(context.serverId, payload.firstValue.serverId)
        assertEquals(sessionId1, payload.firstValue.sessionId)
        assertEquals(msg.chatMessage, payload.firstValue.chatMessage)
    }

    @Test
    fun closedSession() {
        doThrow(IllegalStateException::class).whenever(session2).sendMessage(any())

        processor.process(msg, session1)

        assertFalse(context.sessions.contains(session2))

        verify(session1, never()).sendMessage(any())
        verify(session2).sendMessage(any())
        verify(session3).sendMessage(any())

        val payload = argumentCaptor<MessageSentEventPayload>()
        verify(eventStream).publish(eq(EventURN.MESSAGE_SENT.urn), payload.capture())
    }

    @Test
    fun exception() {
        doThrow(RuntimeException::class).whenever(session2).sendMessage(any())

        processor.process(msg, session1)

        verify(session1, never()).sendMessage(any())
        verify(session2).sendMessage(any())
        verify(session3).sendMessage(any())

        val payload = argumentCaptor<MessageSentEventPayload>()
        verify(eventStream).publish(eq(EventURN.MESSAGE_SENT.urn), payload.capture())
    }
}
