package com.wutsi.platform.rtm.websocket

import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.Collections

@Service
class RTMContext {
    private val sessions: MutableList<WebSocketSession> = Collections.synchronizedList(mutableListOf())

    fun attach(roomId: String, session: WebSocketSession) {
        session.attributes["roomId"] = roomId
        sessions.add(session)
    }

    fun detach(session: WebSocketSession) {
        sessions.remove(session)
    }

    fun findSessionByRoom(roomId: String): List<WebSocketSession> =
        sessions.filter { it.attributes["roomId"] == roomId }
}
