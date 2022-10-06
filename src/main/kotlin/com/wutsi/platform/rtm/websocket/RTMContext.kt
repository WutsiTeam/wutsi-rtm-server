package com.wutsi.platform.rtm.websocket

import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.Collections
import java.util.UUID

@Service
class RTMContext(
    val serverId: String = UUID.randomUUID().toString(),
    val sessions: MutableList<WebSocketSession> = Collections.synchronizedList(mutableListOf())
) {
    fun attach(roomId: String, userId: String?, session: WebSocketSession) {
        session.attributes["roomId"] = roomId
        userId?.let { session.attributes["userId"] = it }
        sessions.add(session)
    }

    fun detach(session: WebSocketSession) {
        sessions.remove(session)
    }

    fun findSessionsByRoom(roomId: String): List<WebSocketSession> =
        sessions.filter { it.attributes["roomId"] == roomId }

    fun findSessionsByUser(userId: String): List<WebSocketSession> =
        sessions.filter { it.attributes["userId"] == userId }
}
