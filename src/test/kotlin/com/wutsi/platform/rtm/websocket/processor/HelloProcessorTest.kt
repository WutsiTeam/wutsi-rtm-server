package com.wutsi.platform.rtm.websocket.processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.dto.MessageType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class HelloProcessorTest {
    @Autowired
    private lateinit var processor: HelloProcessor

    private lateinit var session: WebSocketSession

    private val id = UUID.randomUUID().toString()
    private val attributes = mutableMapOf<String, Any>()

    @BeforeEach
    fun setUp() {
        session = mock()
        doReturn(id).whenever(session).id
        doReturn(attributes).whenever(session).attributes
    }

    @Test
    fun process() {
        val message = Message(type = MessageType.hello, roomId = "1")
        processor.process(message, session)

        val result = argumentCaptor<TextMessage>()
        verify(session).sendMessage(result.capture())

        val payload = ObjectMapper().readValue(result.firstValue.payload, Message::class.java)
        assertEquals(id, payload.sessionId)
        assertEquals(message.roomId, attributes["roomId"])
    }
}
