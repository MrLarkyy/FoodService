package gg.aquatic.foodservice.data.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object BrandTable : Table("public.brand") {
    val id = uuid("id")
    val name = varchar("name", 100)
    val externalId = varchar("external_id", 100).uniqueIndex("brand_external_id_unique").nullable()
    val lastUpdated = datetime("last_updated").nullable()

    override val primaryKey = PrimaryKey(id, name = "brand_pkey")
}
