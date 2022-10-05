package com.wutsi.platform.rtm

import com.wutsi.platform.core.WutsiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.socket.config.annotation.EnableWebSocket

@WutsiApplication
@SpringBootApplication
@EnableWebSocket
public class Application

public fun main(vararg args: String) {
    org.springframework.boot.runApplication<Application>(*args)
}
