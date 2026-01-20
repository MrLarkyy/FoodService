package gg.aquatic.foodservice.routes

import gg.aquatic.foodservice.data.repository.BrandRepository
import org.http4k.core.*
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.routing.routes

/**
 * Modular routing system for potential future expansion with additional routes.
 */
fun appRouter(brandRepository: BrandRepository): HttpHandler {
    return catchAllExceptionFilter()
        .then(
            routes(
                brandRoutes(brandRepository)
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
