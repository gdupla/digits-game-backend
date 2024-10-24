package digits.shared.security

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc // This annotation is important for MockMvc
class SecurityConfigTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = ["USER"])
    fun `authenticated user can access protected endpoint`() {
        mockMvc.get("/protected")
            .andExpect { status { isOk() } }
    }

    @Test
    fun `unauthenticated user cannot access protected endpoint`() {
        mockMvc.get("/protected")
            .andExpect { status { is3xxRedirection() } }
    }
}
