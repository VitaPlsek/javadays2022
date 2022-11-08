package cz.vitaplsek.e2e

import cz.vitaplsek.e2e.jooq.Tables.CLIENT
import cz.vitaplsek.e2e.jooq.Tables.OFFICE
import cz.vitaplsek.e2e.jooq.tables.records.ClientRecord
import cz.vitaplsek.e2e.jooq.tables.records.OfficeRecord
import org.jooq.DSLContext
import kotlin.random.Random

fun DSLContext.createClient(applyFunction: ClientRecord.() -> Unit = {}) =
    newRecord(CLIENT).apply {
        name = "client_" + Random.nextInt()

        apply(applyFunction)
        store()
    }

fun DSLContext.createOffice(client: ClientRecord, applyFunction: OfficeRecord.() -> kotlin.Unit = {}) =
    newRecord(OFFICE)
        .apply {
            location = "location" + Random.nextInt()
            active = true
            clientId = client.id

            apply(applyFunction)
            store()
        }

fun DSLContext.getOffice(id: Long) =
    selectFrom(OFFICE)
        .where(OFFICE.ID.eq(id))
        .fetchSingle()

fun DSLContext.deleteDatabase() {
    deleteFrom(OFFICE).execute()
    deleteFrom(CLIENT).execute()
}
