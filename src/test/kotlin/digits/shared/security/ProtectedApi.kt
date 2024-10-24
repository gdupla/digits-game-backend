package digits.shared.security

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProtectedApi {

    @GetMapping("/protected")
    fun protectedEndpoint(): String {
        return "This is a protected endpoint."
    }
}