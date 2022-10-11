package com.wutsi.platform.rtm.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.core.messaging.MessagingService
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.rtm.model.ChatMessage
import com.wutsi.platform.rtm.model.ChatMessageType
import com.wutsi.platform.rtm.model.ChatUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PushNotificationServiceTest {
    @MockBean
    private lateinit var accountApi: WutsiAccountApi

    @MockBean
    private lateinit var messagingServiceProvider: MessagingServiceProvider

    private lateinit var messaging: MessagingService

    @Autowired
    private lateinit var service: PushNotificationService

    private val msg = ChatMessage(
        id = UUID.randomUUID().toString(),
        roomId = "100",
        type = ChatMessageType.text,
        author = ChatUser(
            id = "100",
            firstName = "Ray",
            lastName = "Sponsible"
        ),
        text = "Hello world"
    )

    @BeforeEach
    fun setUp() {
        messaging = mock()
        doReturn(messaging).whenever(messagingServiceProvider).get(MessagingType.PUSH_NOTIFICATION)
    }

    @Test
    fun `send push notification to user having a fcm-token`() {
        // GIVEN
        val account = Account(fcmToken = "122132")
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        // WHEN
        service.onMessageSent(msg, 100)

        // THEN
        val request = argumentCaptor<com.wutsi.platform.core.messaging.Message>()
        verify(messaging).send(request.capture())

        assertEquals("${msg.author.firstName} ${msg.author.lastName}", request.firstValue.subject)
        assertEquals(msg.text, request.firstValue.body)
        assertEquals(account.fcmToken, request.firstValue.recipient.deviceToken)
    }

    @Test
    fun `dont send push notification to user having a no fcm-token`() {
        // GIVEN
        val account = Account(fcmToken = null)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        // WHEN
        service.onMessageSent(msg, 100)

        // THEN
        verify(messaging, never()).send(any())
    }
}
