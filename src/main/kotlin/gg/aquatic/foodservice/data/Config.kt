package gg.aquatic.foodservice.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object Config {
    private val dataSource: HikariDataSource by lazy {
        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("DB_URL") ?: throw IllegalStateException("DB_URL is missing")
            username = System.getenv("DB_USER") ?: throw IllegalStateException("DB_USER is missing")
            password = System.getenv("DB_PASSWORD") ?: throw IllegalStateException("DB_PASSWORD is missing")
            driverClassName = "org.postgresql.Driver"

            // Better for lambdas - small pool
            maximumPoolSize = 2
            connectionTimeout = 5000
        }
        val ds = HikariDataSource(config)
        ds
    }

    val db: Database by lazy { Database.connect(dataSource) }
}