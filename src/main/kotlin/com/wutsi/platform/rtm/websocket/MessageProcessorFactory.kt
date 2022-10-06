package com.wutsi.platform.rtm.websocket

import com.wutsi.platform.rtm.dto.MessageType
import com.wutsi.platform.rtm.websocket.processor.ByeProcessor
import com.wutsi.platform.rtm.websocket.processor.HelloProcessor
import com.wutsi.platform.rtm.websocket.processor.ReceivedProcessor
import com.wutsi.platform.rtm.websocket.processor.SendProcessor
import org.springframework.stereotype.Service

@Service
class MessageProcessorFactory(
    private val hello: HelloProcessor,
    private val bye: ByeProcessor,
    private val send: SendProcessor,
    private val received: ReceivedProcessor
) {
    fun get(type: MessageType): MessageProcessor =
        when (type) {
            MessageType.hello -> hello
            MessageType.bye -> bye
            MessageType.send -> send
            MessageType.received -> received
            else -> throw IllegalStateException(type.name)
        }
}
