package gg.aquatic.foodservice

import gg.aquatic.foodservice.data.repository.BrandRepository
import gg.aquatic.foodservice.routes.appRouter
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.jetbrains.exposed.sql.Database
import java.io.File
import kotlin.test.Test

class OpenApiExportTest {
    @Test
    fun `export openapi json`() {
        // We don't need a real DB for this, just the router structure
        val db = Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
        val app = appRouter(BrandRepository(db))
        
        val response = app(Request(GET, "/openapi.json"))
        
        val distDir = File("build/dist")
        if (!distDir.exists()) distDir.mkdirs()
        
        File(distDir, "openapi.json").writeText(response.bodyString())
        println("OpenAPI JSON exported to build/dist/openapi.json")
    }
}
