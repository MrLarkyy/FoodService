package gg.aquatic.foodservice

import gg.aquatic.foodservice.data.db.BrandTable
import gg.aquatic.foodservice.data.repository.BrandRepository
import gg.aquatic.foodservice.routes.appRouter
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class BrandAppTest {
    private val db = Database.connect("jdbc:h2:mem:app_test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    private val repository = BrandRepository(db)
    private val app = appRouter(repository)

    @Test
    fun `GET brands returns 200 OK`() {
        transaction(db) {
            SchemaUtils.create(BrandTable)
            BrandTable.insert {
                it[id] = UUID.randomUUID()
                it[name] = "Test Brand"
            }
        }

        val response = app(Request(GET, "/brands"))

        assertEquals(OK, response.status)
        assert(response.bodyString().contains("Test Brand"))
    }

    @Test
    fun `GET brands with filter returns only matching items`() {
        val response = app(Request(GET, "/brands?name=NonExistent"))
        assertEquals(OK, response.status)
        assertEquals("[]", response.bodyString().trim())
    }
}
