package com.wutsi.platform.rtm.websocket

import com.wutsi.platform.rtm.dto.MessageType
import com.wutsi.platform.rtm.websocket.processor.ByeProcessor
import com.wutsi.platform.rtm.websocket.processor.HelloProcessor
import com.wutsi.platform.rtm.websocket.processor.ReceivedProcessor
import com.wutsi.platform.rtm.websocket.processor.SendProcessor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class MessageProcessorFactoryTest {
    @Autowired
    private lateinit var factory: MessageProcessorFactory

    @Test
    fun get() {
        assertTrue(factory.get(MessageType.hello) is HelloProcessor)
        assertTrue(factory.get(MessageType.send) is SendProcessor)
        assertTrue(factory.get(MessageType.bye) is ByeProcessor)
        assertTrue(factory.get(MessageType.received) is ReceivedProcessor)
    }

    @Test
    fun error() {
        assertThrows<IllegalStateException> {
            factory.get(MessageType.unknown)
        }
    }
}
