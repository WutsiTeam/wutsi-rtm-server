package com.wutsi.platform.rtm.service

import com.wutsi.platform.core.security.spring.ApiKeyAuthenticator
import org.springframework.stereotype.Service

@Service
class ApplicationAuthenticator : ApiKeyAuthenticator {
    override fun authenticate(apiKey: String): String =
        ""
}
