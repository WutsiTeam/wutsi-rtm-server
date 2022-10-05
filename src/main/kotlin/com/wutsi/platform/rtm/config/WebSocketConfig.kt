package com.wutsi.platform.rtm.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.rtm.websocket.MessageProcessorFactory
import com.wutsi.platform.rtm.websocket.RTMTextWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
class WebSocketConfig(
    private val messageProcessorFactory: MessageProcessorFactory,
    private val objectMapper: ObjectMapper
) : WebSocketConfigurer {
    @Bean
    fun rtmTextWebSocketHandler(): RTMTextWebSocketHandler =
        RTMTextWebSocketHandler(messageProcessorFactory, objectMapper)

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(rtmTextWebSocketHandler(), "/rtm")
    }
}
