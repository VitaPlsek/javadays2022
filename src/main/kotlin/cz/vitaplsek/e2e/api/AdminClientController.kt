package cz.vitaplsek.e2e.api

import cz.vitaplsek.e2e.api.dto.CreateOfficeDto
import cz.vitaplsek.e2e.domain.ClientDao
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminClientController(val clientDao: ClientDao) {

    @GetMapping("/client/{id}")
    fun get(@PathVariable(value = "id") id: Long) = clientDao.get(id)

    @PostMapping("/client/{id}/office")
    fun createOffice(
        @PathVariable(value = "id") id: Long,
        @RequestBody createOffice: CreateOfficeDto
    ) = clientDao.createOffice(id, createOffice)

    @PostMapping("/office/{id}/deactivate")
    fun deactivateOffice(
        @PathVariable(value = "id") id: Long
    ) = clientDao.deactivateOffice(id)
}
