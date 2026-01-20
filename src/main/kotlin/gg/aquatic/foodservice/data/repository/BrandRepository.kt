package gg.aquatic.foodservice.data.repository

import gg.aquatic.foodservice.data.db.BrandTable
import gg.aquatic.foodservice.data.model.Brand
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class BrandRepository(private val db: Database) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun findBrands(
        name: String?,
        externalId: String?,
        sortBy: String?,
        sortDirection: String?
    ): List<Brand> = transaction(db) {
        logger.debug(
            "Finding brands with name={}, externalId={}, sortBy={}, sortDirection={}",
            name, externalId, sortBy, sortDirection
        )
        val query = BrandTable.selectAll()

        // Case-insensitive search
        name?.let {
            query.andWhere { BrandTable.name.lowerCase() like "%${it.lowercase()}%" }
        }

        // Exact match on external ID
        externalId?.let {
            query.andWhere { BrandTable.externalId eq it }
        }

        val direction = if (sortDirection?.lowercase() == "desc") SortOrder.DESC else SortOrder.ASC

        val orderColumn = when (sortBy?.lowercase()) {
            "name" -> BrandTable.name
            "last_updated" -> BrandTable.lastUpdated
            else -> BrandTable.name
        }

        query.orderBy(orderColumn to direction)

        query.map {
            Brand(
                id = it[BrandTable.id],
                name = it[BrandTable.name],
                externalId = it[BrandTable.externalId],
                lastUpdated = it[BrandTable.lastUpdated]
            )
        }
    }
}
