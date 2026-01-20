package gg.aquatic.foodservice

import gg.aquatic.foodservice.data.db.BrandTable
import gg.aquatic.foodservice.data.repository.BrandRepository
import gg.aquatic.foodservice.routes.appRouter
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals

class BrandAppTest {
    private val db = Database.connect("jdbc:h2:mem:app_test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    private val repository = BrandRepository(db)
    private val app = appRouter(repository)

    @Test
    fun `GET brands returns 200 OK`() {
        transaction(db) { SchemaUtils.create(BrandTable) }

        val response = app(Request(GET, "/brands"))

        assertEquals(OK, response.status)

        val contentType = response.header("Content-Type") ?: ""
        assert(contentType.startsWith("application/json")) { "Expected application/json but got $contentType" }
    }
}
