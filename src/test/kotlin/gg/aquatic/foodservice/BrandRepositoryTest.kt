package gg.aquatic.foodservice

import gg.aquatic.foodservice.data.db.BrandTable
import gg.aquatic.foodservice.data.repository.BrandRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BrandRepositoryTest {
    private val db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    private val repository = BrandRepository(db)

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
    fun `test filtering by name case-insensitive`() {
        val results = repository.findBrands(name = "pPl", null, null, null)
        assertEquals(1, results.size)
        assertEquals("Apple", results[0].name)
    }

    @Test
    fun `test sorting`() {
        val results = repository.findBrands(null, null, "name", "desc")
        assertEquals("Banana", results[0].name)
    }
}
