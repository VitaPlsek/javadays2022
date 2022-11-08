package cz.vitaplsek.e2e.api

import cz.vitaplsek.e2e.E2EShouldSpec
import cz.vitaplsek.e2e.api.dto.CreateOfficeDto
import cz.vitaplsek.e2e.createClient
import cz.vitaplsek.e2e.createOffice
import cz.vitaplsek.e2e.deactivateOffice
import cz.vitaplsek.e2e.getClient
import cz.vitaplsek.e2e.getClientRaw
import cz.vitaplsek.e2e.getOffice
import cz.vitaplsek.e2e.is4xxClientError
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class AdminClientControllerTest : E2EShouldSpec() {

    init {
        context("client") {

            val client = dslContext.createClient { name = "first" }

            should("get existing client") {
                testClient.getClient(client.id).name shouldBe "first"
            }

            should("not get existing client") {
                testClient.getClientRaw(424242L)
                    .is4xxClientError
            }

            context("office") {
                val createdOffice = testClient.createOffice(client.id, createOfficeDto())

                context("when deactivated") {
                    testClient.deactivateOffice(createdOffice)

                    should("be set as inactive in db") {
                        dslContext.getOffice(createdOffice.id).active shouldBe false
                    }

                    should("not be read with client") {
                        val clientWithOffices = testClient.getClient(client.id)
                        clientWithOffices.offices shouldHaveSize 0
                    }
                }

                should("be read with client") {
                    val clientWithOffices = testClient.getClient(client.id)
                    clientWithOffices.offices shouldHaveSize 1
                    clientWithOffices.offices.first().id shouldBe createdOffice.id
                }
            }
        }
    }

    private fun createOfficeDto() = CreateOfficeDto(
        location = "location" + Random.nextInt(),
        active = true
    )
}
