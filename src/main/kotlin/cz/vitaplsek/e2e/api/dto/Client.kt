package cz.vitaplsek.e2e.api.dto

data class ClientDto(
    val id: Long,
    val name: String,

    val offices: List<OfficeDto> = emptyList()
)

data class OfficeDto(
    val id: Long,
    val location: String,
    val active: Boolean
)

data class CreateOfficeDto(
    val location: String,
    val active: Boolean
)
