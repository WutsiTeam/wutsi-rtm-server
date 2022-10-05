package com.wutsi.platform.rtm.endpoint

import com.wutsi.platform.rtm.`delegate`.HelloDelegate
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class HelloController(
    public val `delegate`: HelloDelegate
) {
    @GetMapping("/v1/hello")
    public fun invoke() {
        delegate.invoke()
    }
}
