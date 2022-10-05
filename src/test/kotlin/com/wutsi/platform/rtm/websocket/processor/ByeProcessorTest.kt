package com.wutsi.platform.rtm.websocket.processor

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.dto.MessageType
import com.wutsi.platform.rtm.websocket.RTMContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ByeProcessorTest {
    @Autowired
    private lateinit var processor: ByeProcessor

    @MockBean
    private lateinit var context: RTMContext

    private lateinit var session: WebSocketSession

    @BeforeEach
    fun setUp() {
        session = mock()
    }

    @Test
    fun process() {
        val message = Message(type = MessageType.hello, roomId = "1")
        processor.process(message, session)

        val result = argumentCaptor<TextMessage>()
        verify(context).detach(session)
    }
}
