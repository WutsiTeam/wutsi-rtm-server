package com.wutsi.platform.rtm.websocket.processor

import com.wutsi.platform.rtm.dto.Message
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class ByeProcessor : AbstractMessageProcessor() {
    override fun process(message: Message, session: WebSocketSession) {
        context.detach(session)
    }
}
