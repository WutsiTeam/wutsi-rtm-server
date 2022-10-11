package com.wutsi.platform.rtm.service

import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.Party
import com.wutsi.platform.rtm.model.ChatMessage
import org.springframework.stereotype.Service

@Service
class PushNotificationService(
    private val logger: KVLogger,
    private val accountApi: WutsiAccountApi,
    private val messagingServiceProvider: MessagingServiceProvider
) {
    fun onMessageSent(message: ChatMessage, recipientId: Long) {
        val recipient = accountApi.getAccount(recipientId).account
        if (recipient.fcmToken == null) {
            return
        }

        val gateway = messagingServiceProvider.get(MessagingType.PUSH_NOTIFICATION)
        val messageId = gateway.send(
            Message(
                recipient = Party(
                    displayName = recipient.displayName,
                    deviceToken = recipient.fcmToken
                ),
                mimeType = "text/plain",
                subject = "${message.author.firstName} ${message.author.lastName}",
                body = message.text ?: ""
            )
        )
        logger.add("fcm_message_id", messageId)
    }
}
