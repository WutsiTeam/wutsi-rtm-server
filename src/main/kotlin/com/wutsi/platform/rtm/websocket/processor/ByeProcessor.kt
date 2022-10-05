package com.wutsi.platform.rtm.websocket.processor

import com.wutsi.platform.rtm.dto.Message
import com.wutsi.platform.rtm.websocket.RTMContext
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class ByeProcessor(
    private val context: RTMContext
) : AbstractMessageProcessor() {
    override fun process(message: Message, session: WebSocketSession) {
        context.detach(session)
    }
}
