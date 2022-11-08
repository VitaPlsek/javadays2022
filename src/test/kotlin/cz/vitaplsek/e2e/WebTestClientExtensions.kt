package cz.vitaplsek.e2e

import cz.vitaplsek.e2e.api.dto.ClientDto
import cz.vitaplsek.e2e.api.dto.CreateOfficeDto
import cz.vitaplsek.e2e.api.dto.OfficeDto
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

fun WebTestClient.createOffice(id: Long, createOfficeDto: CreateOfficeDto) =
    post().uri("/client/$id/office")
        .bodyValue(createOfficeDto)
        .is2xxSuccessful
        .returnBody(OfficeDto::class.java)

fun WebTestClient.deactivateOffice(officeDto: OfficeDto) =
    post().uri("/office/${officeDto.id}/deactivate")
        .is2xxSuccessful

fun WebTestClient.getClient(id: Long) =
    getClientRaw(id)
        .is2xxSuccessful
        .returnBody(ClientDto::class.java)

fun WebTestClient.getClientRaw(id: Long): WebTestClient.RequestHeadersSpec<*> =
    get().uri("/client/$id")

private fun <T> WebTestClient.ResponseSpec.returnBody(clazz: Class<T>) =
    this.expectBody(clazz)
        .returnResult()
        .responseBody!!

val WebTestClient.RequestHeadersSpec<*>.is2xxSuccessful
    get() = exchange()
        .expectStatus()
        .is2xxSuccessful

val WebTestClient.RequestHeadersSpec<*>.is4xxClientError
    get() = exchange()
        .expectStatus()
        .is4xxClientError

