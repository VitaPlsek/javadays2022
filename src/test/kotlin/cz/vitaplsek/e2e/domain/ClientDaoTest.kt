package cz.vitaplsek.e2e.domain

import cz.vitaplsek.e2e.DaoShouldSpec
import cz.vitaplsek.e2e.createClient
import cz.vitaplsek.e2e.createOffice
import cz.vitaplsek.e2e.jooq.Tables
import io.kotest.matchers.collections.exist
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe

class ClientDaoTest(val clientDao: ClientDao) : DaoShouldSpec() {

    init {
        context("get client") {

            val client = dslContext.createClient()
            val office = dslContext.createOffice(client)
            val inactiveOffice = dslContext.createOffice(client) { active = false }

            should("get client with its offices") {
                val clientFromDb = clientDao.get(client.id)
                clientFromDb shouldNotBe null
                clientFromDb.name shouldBe client.name

                clientFromDb.offices shouldExist { it.id == office.id }
            }

            should("not get inactive offices") {
                val clientFromDb = clientDao.get(client.id)

                clientFromDb.offices shouldNot exist { it.id == inactiveOffice.id }
            }
        }
    }
}
