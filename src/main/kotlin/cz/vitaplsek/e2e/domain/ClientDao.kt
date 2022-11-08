package cz.vitaplsek.e2e.domain

import cz.vitaplsek.e2e.api.dto.ClientDto
import cz.vitaplsek.e2e.api.dto.CreateOfficeDto
import cz.vitaplsek.e2e.api.dto.OfficeDto
import cz.vitaplsek.e2e.jooq.Tables.CLIENT
import cz.vitaplsek.e2e.jooq.Tables.OFFICE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.springframework.stereotype.Component

@Component
class ClientDao(val dslContext: DSLContext) {

    fun get(id: Long): ClientDto =
        dslContext
            .select(
                CLIENT.asterisk(),
                multiset(
                    DSL.selectFrom(OFFICE)
                        .where(OFFICE.CLIENT_ID.eq(id), OFFICE.ACTIVE.isTrue)
                ).`as`(ClientDto::offices.name).convertFrom { it.into(OfficeDto::class.java) }
            )
            .from(CLIENT)
            .where(CLIENT.ID.eq(id))
            .fetchSingleInto(ClientDto::class.java)

    fun createOffice(id: Long, createOffice: CreateOfficeDto) =
        dslContext.newRecord(OFFICE)
            .apply {
                from(createOffice)
                clientId = id

                store()
            }
            .into(OfficeDto::class.java)

    fun deactivateOffice(id: Long) =
        dslContext.update(OFFICE)
            .set(OFFICE.ACTIVE, false)
            .where(OFFICE.ID.eq(id))
            .execute()
}
