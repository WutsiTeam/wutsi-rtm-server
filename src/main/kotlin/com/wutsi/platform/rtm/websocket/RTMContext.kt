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
    companion object {
        const val KEY_USER_ID = "userId"
        const val KEY_ROOM_ID = "roomId"
    }

    fun attach(roomId: String, userId: String?, session: WebSocketSession) {
        session.attributes["roomId"] = roomId
        userId?.let { session.attributes["userId"] = it }
        sessions.add(session)
    }

    fun detach(session: WebSocketSession) {
        sessions.remove(session)
    }

    fun findSessionsByRoom(roomId: String): List<WebSocketSession> =
        sessions.filter { it.attributes[KEY_ROOM_ID] == roomId }

    fun findSessionsByUser(userId: String): List<WebSocketSession> =
        sessions.filter { it.attributes[KEY_USER_ID] == userId }

    fun getUserId(session: WebSocketSession): String? =
        session.attributes[KEY_USER_ID]?.toString()
}
