package gg.aquatic.foodservice.routes

import gg.aquatic.foodservice.data.repository.BrandRepository
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.*
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.format.KotlinxSerialization
import org.http4k.routing.routes

/**
 * Modular routing system for potential future expansion with additional routes.
 */
fun appRouter(brandRepository: BrandRepository): HttpHandler {
    val apiContract = contract {
        renderer = OpenApi3(ApiInfo("Brand Service API", "v1.0"), KotlinxSerialization)
        descriptionPath = "/openapi.json"
        routes += brandRoutes(brandRepository)
    }

    return catchAllExceptionFilter()
        .then(
            routes(
                apiContract
            )
        )
}

private fun catchAllExceptionFilter() = Filter { next ->
    { req ->
        try {
            next(req)
        } catch (e: Exception) {
            Response(INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/json")
                .body("""{"error": "${e.message ?: "Unknown error"}"}""")
        }
    }
}
