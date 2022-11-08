package cz.vitaplsek.e2e

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.scopes.ShouldSpecContainerScope
import io.kotest.extensions.spring.SpringExtension
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition

@SpringBootTest
@ActiveProfiles("test")
open class DaoShouldSpec : ShouldSpec() {

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var transactionManager: PlatformTransactionManager

    @Autowired
    lateinit var dslContext: DSLContext

    lateinit var transactionStatus: TransactionStatus
    var transactionLevel = 0

    final override fun context(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
        val transactional: suspend ShouldSpecContainerScope.() -> Unit = {
            if (transactionLevel == 0) {
                transactionStatus = transactionManager.open()
            }

            transactionLevel++

            test()

            transactionLevel--

            if (transactionLevel == 0) {
                transactionManager.rollback(transactionStatus)
            }
        }

        super.context(name, transactional)
    }
}

private fun PlatformTransactionManager.open(): TransactionStatus {
    val definition = DefaultTransactionDefinition()
    definition.isolationLevel = TransactionDefinition.ISOLATION_REPEATABLE_READ
    definition.timeout = 1000
    return getTransaction(definition)
}
