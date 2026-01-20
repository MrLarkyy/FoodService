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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BrandAppTest {
    private val db = Database.connect(
        "jdbc:h2:mem:app_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;",
        driver = "org.h2.Driver",
        user = "sa",
        password = ""
    )
    private val repository = BrandRepository(db)
    private val app = appRouter(repository)

    @BeforeTest
    fun setup() {
        transaction(db) {
            SchemaUtils.drop(BrandTable)
            SchemaUtils.create(BrandTable)

            BrandTable.insert {
                it[id] = UUID.randomUUID()
                it[name] = "Apple"
                it[externalId] = "ext-1"
            }
            BrandTable.insert {
                it[id] = UUID.randomUUID()
                it[name] = "Banana"
                it[externalId] = "ext-2"
            }
        }
    }

    @Test
    fun `GET brands returns 200 OK`() {
        val response = app(Request(GET, "/brands"))

        assertEquals(OK, response.status)
        assert(response.bodyString().contains("Apple"))
    }

    @Test
    fun `GET brands with filter returns only matching items`() {
        val response = app(Request(GET, "/brands?name=NonExistent"))
        assertEquals(OK, response.status)
        assertEquals("[]", response.bodyString().trim())
    }

    @Test
    fun `test sorting`() {
        val results = repository.findBrands(null, null, "name", "desc")
        assertEquals("Banana", results[0].name)
    }

    @Test
    fun `test filtering by externalId exact match`() {
        val results = repository.findBrands(null, "ext-1", null, null)
        assertEquals(1, results.size)
        assertEquals("ext-1", results[0].externalId)
    }

    @Test
    fun `test combined filtering by name and externalId`() {
        // Should find Apple
        val found = repository.findBrands(name = "Apple", externalId = "ext-1", null, null)
        assertEquals(1, found.size)

        // Should find nothing (Apple name with Banana externalId)
        val notFound = repository.findBrands(name = "Apple", externalId = "ext-2", null, null)
        assertEquals(0, notFound.size)
    }

    @Test
    fun `test sorting by name ascending default`() {
        val results = repository.findBrands(null, null, "name", "asc")
        assertEquals("Apple", results[0].name)
        assertEquals("Banana", results[1].name)
    }

    @Test
    fun `test filtering with no results`() {
        val results = repository.findBrands(name = "Zucchini", null, null, null)
        assertEquals(0, results.size)
    }
}
