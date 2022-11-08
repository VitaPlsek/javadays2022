package cz.vitaplsek.e2e

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
open class E2EShouldSpec : ShouldSpec() {

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var testClient: WebTestClient

    @Autowired
    lateinit var dslContext: DSLContext

    init {
        afterSpec {
            dslContext.deleteDatabase()
        }
    }
}
